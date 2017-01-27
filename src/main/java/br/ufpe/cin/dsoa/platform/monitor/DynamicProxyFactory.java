package br.ufpe.cin.dsoa.platform.monitor;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.event.EventDistribuitionService;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.api.service.impl.ServiceInstanceProxyImpl;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.DsoaUtil;

/**
 * A Service Proxy that intercepts requests at the client side. It generates
 * events that represent dependency invocation (InvocationEvent) and forwards
 * them to the Event Processing Center. There some Property Computing Agents are
 * responsible for metric derivation.
 * 
 * @author fabions
 * 
 */
// TODO REMOVE FROM THE PLATFORM, IT IS PART OF THE BindingManager
public class DynamicProxyFactory implements ProxyFactory {

	private static Logger logger;
	{
		java.util.logging.Formatter f = new java.util.logging.Formatter() {

			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}
		};

		logger = Logger.getLogger("InvocationProxyLogger");
		try {
			FileHandler invocationLogFile = new FileHandler(DsoaUtil.getLoggerName(this.getClass().getCanonicalName()));
			invocationLogFile.setFormatter(f);
			logger.addHandler(invocationLogFile);
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Object proxy;
	private EventDistribuitionService distribuitionService;
	
	public Object getProxy(String consumerId, ServiceInstance serviceInstance) {
		if (this.proxy == null) {
			DynamicProxy dynaProxy = new DynamicProxy(consumerId, serviceInstance);
			
			ClassLoader cl = this.getClass().getClassLoader();
			String itfClassname = serviceInstance.getPort().getServiceSpecification().getFunctionalInterface().getInterfaceName();
			Class<?> itfClass;
			try {
				itfClass = cl.loadClass(itfClassname);
				proxy = java.lang.reflect.Proxy.newProxyInstance(cl,
						new Class[] { itfClass }, dynaProxy);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} 
		return proxy;
	}

	class DynamicProxy implements InvocationHandler {

		private String consumerId;
		private String serviceId;
		
		private Object serviceObject;
		private ServiceReference serviceReference;

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

		public DynamicProxy(String consumerId, ServiceInstance serviceInstance) {
			this.consumerId = consumerId;
			this.serviceId = serviceInstance.getName();
			this.serviceReference = ((ServiceInstanceProxyImpl)serviceInstance).getServiceReference();
			try {
				m_hashCodeMethod = Object.class.getMethod("hashCode", null);
				m_equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
				m_toStringMethod = Object.class.getMethod("toString", null);
			} catch (NoSuchMethodException e) {
				throw new NoSuchMethodError(e.getMessage());
			}
			
		}
		
		public synchronized Object getServiceObject() {
			if (this.serviceObject == null && 
					null != serviceReference) {
				serviceObject = serviceReference.getBundle().getBundleContext()
						.getService(serviceReference);
			}
			return serviceObject;
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
				synchronized (serviceReference) {
						result = method.invoke(this.getServiceObject(), args);
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
				if (getServiceObject() != null) {

					Map<String, String> parameterTypes = new HashMap<String, String>();
					Map<String, Object> parameterValues = new HashMap<String, Object>();

					for (int i = 0; i < method.getParameterTypes().length; i++) {
						parameterTypes.put(i + "", method.getParameterTypes()[i].getName());
						parameterValues.put(i + "", args[i]);
					}
					String returnType = method.getReturnType().getName();

					notifyInvocation(consumerId, serviceId, method.getName(),
							requestTime, responseTime, success, exceptionClassName,
							exceptionMessage, parameterTypes, parameterValues, returnType,
							result);
					logger.info(serviceId+","+ System.currentTimeMillis()+"," + (responseTime - requestTime));
				}
			}
			return result;
		}

		private void notifyInvocation(String consumerId, String serviceId, String operationName,
				long requestTimestamp, long responseTimestamp, boolean success,
				String exceptionClass, String exceptionMessage, Map<String, String> parameterTypes,
				Map<String, Object> parameterValues, String returnType, Object returnValue) {

			String source = String.format("%s%s%s", serviceId, Constants.TOKEN, operationName);

			Map<String, Object> metadata = this.loadInvocationMetadata(source);
			Map<String, Object> data = this.loadInvocationData(consumerId, serviceId,
					operationName, requestTimestamp, responseTimestamp, success, exceptionClass,
					exceptionMessage, parameterTypes, parameterValues, returnType, returnValue);

			distribuitionService.postEvent(Constants.INVOCATION_EVENT, metadata, data);
			
		}

		private Map<String, Object> loadInvocationMetadata(String source) {
			Map<String, Object> metadata = new HashMap<String, Object>();
			metadata.put("source", source);

			return metadata;
		}

		private Map<String, Object> loadInvocationData(String consumerId, String serviceId,
				String operationName, long requestTimestamp, long responseTimestamp,
				boolean success, String exceptionClass, String exceptionMessage,
				Map<String, String> parameterTypes, Map<String, Object> parameterValues,
				String returnType, Object returnValue) {

			Map<String, Object> data = new HashMap<String, Object>();

			data.put(Constants.CONSUMER_ID, consumerId);
			data.put(Constants.SERVICE_ID, serviceId);
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