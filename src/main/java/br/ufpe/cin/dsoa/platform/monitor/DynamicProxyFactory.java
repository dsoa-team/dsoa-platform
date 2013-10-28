package br.ufpe.cin.dsoa.platform.monitor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.osgi.service.event.EventAdmin;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.platform.handler.dependency.Dependency;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.Util;

/**
 * A Service Proxy that intercepts requests at the client side. It generates
 * events that represent dependency invocation (InvocationEvent) and forwards
 * them to the Event Processing Center. There some Property Computing Agents are
 * responsible for metric derivation.
 * 
 * @author fabions
 * 
 */
public class DynamicProxyFactory implements InvocationHandler {

	private Logger log;

	private Dependency dependency;

	private ExecutorService executorService;

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

	private EventType eventType;

	private EventAdmin eventAdmin;

	public DynamicProxyFactory(Dependency dependency, EventAdmin eventAdmin, EventType eventType) {
		this.dependency = dependency;
		this.eventAdmin = eventAdmin;
		this.eventType = eventType;
		this.executorService = Executors.newFixedThreadPool(10);
		this.log = Logger.getLogger(getClass().getSimpleName());

		try {
			m_hashCodeMethod = Object.class.getMethod("hashCode", null);
			m_equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
			m_toStringMethod = Object.class.getMethod("toString", null);
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodError(e.getMessage());
		}
	}

	public Object getProxy() {
		return java.lang.reflect.Proxy.newProxyInstance(
				this.dependency.getClass().getClassLoader(), new Class[] { this.dependency
						.getSpecification().getClazz() }, this);
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
	 * @throws Exception
	 *             if the invocation throws an exception
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
		Class<?> declaringClass = method.getDeclaringClass();
		String exceptionMessage = null;
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
			synchronized (dependency) {
				if(dependency.getService() != null){
					result = method.invoke(dependency.getService().getServiceObject(), args);
				}
			}
		} catch (Exception exc) {
			exceptionMessage = exc.getMessage();
			success = false;
			throw exc;
		} finally {
			responseTime = System.currentTimeMillis();
			synchronized (dependency) {
				if(dependency.getService() != null){
					notifyInvocation(dependency.getComponentId(), dependency.getService().getCompomentId(),
							method.getName(), requestTime, responseTime, success, exceptionMessage);
				}
			}
		}
		return result;
	}

	private void notifyInvocation(String consumerId, String serviceId, String operationName,
			long requestTimestamp, long responseTimestamp, boolean success, String exceptionMessage) {

		NotificationWorker worker = new NotificationWorker(consumerId, serviceId, operationName,
				requestTimestamp, responseTimestamp, success, exceptionMessage);
		this.executorService.execute(worker);
	}

	class NotificationWorker implements Runnable {

		private String consumerId;
		private String serviceId;
		private String operationName;
		private long requestTimestamp;
		private long responseTimestamp;
		private boolean success;
		private String exceptionMessage;

		public NotificationWorker(String consumerId, String serviceId, String operationName,
				long requestTimestamp, long responseTimestamp, boolean success,
				String exceptionMessage) {
			super();
			this.consumerId = consumerId;
			this.serviceId = serviceId;
			this.operationName = operationName;
			this.requestTimestamp = requestTimestamp;
			this.responseTimestamp = responseTimestamp;
			this.success = success;
			this.exceptionMessage = exceptionMessage;
		}

		@Override
		public void run() {
			this.notifyInvocation();
		}

		private void notifyInvocation() {

			String source = String.format("%s%s%s", serviceId, Constants.TOKEN, operationName);

			Map<String, Object> metadata = this.loadInvocationMetadata(source);

			Map<String, Object> data = this.loadInvocationData(consumerId, serviceId,
					operationName, requestTimestamp, responseTimestamp, success, exceptionMessage);

			Event invocationEvent = eventType.createEvent(metadata, data);
			String topic = Util.getDsoaEventTopic(invocationEvent.getEventType());

			Map<String, Event> eventTable = new HashMap<String, Event>();
			eventTable.put(Constants.DSOA_EVENT, invocationEvent);
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
				boolean success, String exceptionMessage) {
			Map<String, Object> data = new HashMap<String, Object>();

			data.put("consumerId", consumerId);
			data.put("serviceId", serviceId);
			data.put("operationName", operationName);
			data.put("requestTimestamp", requestTimestamp);
			data.put("responseTimestamp", responseTimestamp);
			data.put("success", success);
			data.put("exceptionMessage", exceptionMessage);

			return data;
		}
	}

}
