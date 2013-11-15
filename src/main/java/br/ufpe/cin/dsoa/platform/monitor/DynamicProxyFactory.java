package br.ufpe.cin.dsoa.platform.monitor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.service.event.EventAdmin;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.platform.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.util.Constants;

/**
 * A Service Proxy that intercepts requests at the client side. It generates
 * events that represent dependency invocation (InvocationEvent) and forwards
 * them to the Event Processing Center. There some Property Computing Agents are
 * responsible for metric derivation.
 * 
 * @author fabions
 * 
 */
public class DynamicProxyFactory implements ProxyFactory {

	private EventTypeCatalog eventTypeCatalog;
	private EventAdmin eventAdmin;

	private EventType invocationEventType;
	private ExecutorService executorService;

	public void start() {
		this.executorService = Executors.newFixedThreadPool(10);
	}

	public Object getProxy(String consumerId, Service service) {
		if (this.invocationEventType == null) {
			this.invocationEventType = this.eventTypeCatalog.get(Constants.INVOCATION_EVENT);
		}
		DynamicProxy dynaProxy = new DynamicProxy(consumerId, service);
		return java.lang.reflect.Proxy.newProxyInstance(this.getClass().getClassLoader(),
				new Class[] { service.getSpecification().getClazz() }, dynaProxy);
	}

	class DynamicProxy implements InvocationHandler {

		private String consumerId;
		private Service service;

		/**
		 * HashCode method.
		 */
		private Method m_hashCodeMethod;

		/**
		 * Equals method.
		 */
		private Method m_equalsMethod;

		/**
		 * toStirng method.
		 */
		private Method m_toStringMethod;

		public DynamicProxy(String consumerId, Service service) {
			this.consumerId = consumerId;
			this.service = service;

			try {
				m_hashCodeMethod = Object.class.getMethod("hashCode", null);
				m_equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
				m_toStringMethod = Object.class.getMethod("toString", null);
			} catch (NoSuchMethodException e) {
				throw new NoSuchMethodError(e.getMessage());
			}

		}

		/**
		 * Invocation Handler delegating invocation on the dependency object.
		 * 
		 * @param proxy
		 *            the proxy object
		 * @param method
		 *            the method
		 * @param args
		 *            the arguments
		 * @return a proxy object.
		 * @throws Throwable
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
		 *      java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Class<?> declaringClass = method.getDeclaringClass();
			String exceptionMessage = null;
			String exceptionClassName = null;

			if (declaringClass == Object.class) {
				if (method.equals(m_hashCodeMethod)) {
					return new Integer(this.hashCode());
				} else if (method.equals(m_equalsMethod)) {
					return proxy == args[0] ? Boolean.TRUE : Boolean.FALSE;
				} else if (method.equals(m_toStringMethod)) {
					return this.toString();
				} else {
					throw new InternalError("Unexpected Object method dispatched: " + method);
				}
			}

			// InvocationEvent ie = null;
			long requestTime = System.currentTimeMillis(), responseTime;
			Object result = null;
			boolean success = true;
			try {
				synchronized (service) {
					if (service.getServiceObject() != null) {
						result = method.invoke(service.getServiceObject(), args);
					}
				}
			} catch (InvocationTargetException e) {
				Throwable rawException = e.getTargetException();
				exceptionClassName = rawException.getClass().getName();
				exceptionMessage = rawException.getMessage();
				success = false;
				// rawException.printStackTrace();
				throw rawException;

			} catch (Exception exc) {
				exceptionClassName = exc.getClass().getName();
				exceptionMessage = exc.getMessage();
				success = false;
				// exc.printStackTrace();
				throw exc;
			} finally {
				responseTime = System.currentTimeMillis();
				synchronized (service) {
					if (service.getServiceObject() != null) {

						Map<String, String> parameterTypes = new HashMap<String, String>();
						Map<String, Object> parameterValues = new HashMap<String, Object>();

						for (int i = 0; i < method.getParameterTypes().length; i++) {
							parameterTypes.put(i+"", method.getParameterTypes()[i].getName());
							parameterValues.put(i+"", args[i]);
						}
						String returnType = method.getReturnType().getName();

						notifyInvocation(consumerId, service.getProviderId(), method.getName(),
								requestTime, responseTime, success, exceptionClassName,
								exceptionMessage, parameterTypes, parameterValues, returnType,
								result);
					}
				}
			}
			return result;
		}

		private void notifyInvocation(String consumerId, String serviceId, String operationName,
				long requestTimestamp, long responseTimestamp, boolean success,
				String exceptionClass, String exceptionMessage,
				Map<String, String> parameterTypes, Map<String, Object> parameterValues,
				String returnType, Object returnValue) {

			NotificationWorker worker = new NotificationWorker(consumerId, serviceId,
					operationName, requestTimestamp, responseTimestamp, success, exceptionClass,
					exceptionMessage, parameterTypes, parameterValues, returnType, returnValue);
			executorService.execute(worker);
		}

		class NotificationWorker implements Runnable {

			private String consumerId;
			private String serviceId;
			private String operationName;
			private long requestTimestamp;
			private long responseTimestamp;
			private boolean success;
			private String exceptionMessage;
			private String exceptionClass;
			private Map<String, String> parameterTypes;
			private Map<String, Object> parameterValues;
			private String returnType;
			private Object returnValue;

			public NotificationWorker(String consumerId, String serviceId, String operationName,
					long requestTimestamp, long responseTimestamp, boolean success,
					String exceptionMessage, String exceptionClass,
					Map<String, String> parameterTypes, Map<String, Object> parameterValues,
					String returnType, Object returnValue) {
				super();
				this.consumerId = consumerId;
				this.serviceId = serviceId;
				this.operationName = operationName;
				this.requestTimestamp = requestTimestamp;
				this.responseTimestamp = responseTimestamp;
				this.success = success;
				this.exceptionMessage = exceptionMessage;
				this.exceptionClass = exceptionClass;
				this.parameterTypes = parameterTypes;
				this.parameterValues = parameterValues;
				this.returnType = returnType;
				this.returnValue = returnValue;
			}

			@Override
			public void run() {
				this.notifyInvocation();
			}

			private void notifyInvocation() {

				String source = String.format("%s%s%s", serviceId, Constants.TOKEN, operationName);

				Map<String, Object> metadata = this.loadInvocationMetadata(source);

				Map<String, Object> data = this.loadInvocationData(consumerId, serviceId,
						operationName, requestTimestamp, responseTimestamp, success,
						exceptionClass, exceptionMessage);

				Event invocationEvent = invocationEventType.createEvent(metadata, data);
				String topic = invocationEvent.getEventType().getName();

				Map<String, Object> eventTable = new HashMap<String, Object>();
				eventTable.put(topic, invocationEvent.toMap());
				eventAdmin.postEvent(new org.osgi.service.event.Event(topic, eventTable));
			}

			private Map<String, Object> loadInvocationMetadata(String source) {
				Map<String, Object> metadata = new HashMap<String, Object>();

				metadata.put("id", UUID.randomUUID().toString());
				metadata.put("timestamp", System.nanoTime());
				metadata.put("source", source);

				return metadata;
			}

			private Map<String, Object> loadInvocationData(String consumerId, String serviceId,
					String operationName, long requestTimestamp, long responseTimestamp,
					boolean success, String exceptionClass, String exceptionMessage) {
				Map<String, Object> data = new HashMap<String, Object>();

				data.put("consumerId", consumerId);
				data.put("serviceId", serviceId);
				data.put("operationName", operationName);
				data.put("requestTimestamp", requestTimestamp);
				data.put("responseTimestamp", responseTimestamp);
				data.put("success", success);

				if (exceptionClass != null) {
					data.put("exceptionMessage", exceptionMessage);
					data.put("exceptionClass", exceptionClass);
				}
				data.put("parameterTypes", parameterTypes);
				data.put("parameterValues", parameterValues);
				data.put("returnType", returnType);
				data.put("returnValue", returnValue);

				return data;
			}
		}

	}

}