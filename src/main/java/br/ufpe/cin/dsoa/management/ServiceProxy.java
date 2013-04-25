package br.ufpe.cin.dsoa.management;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import br.ufpe.cin.dsoa.event.InvocationEvent;
import br.ufpe.cin.dsoa.handler.dependency.contract.ServiceProvider;

/**
 * A Service Proxy that intercepts requests at the client side. It generates
 * events that represent service invocation (InvocationEvent) and forwards them
 * to the Event Processing Center. There some Metric Computing Agents are
 * responsible for metric derivation.
 * 
 * @author fabions
 * 
 */
public class ServiceProxy implements InvocationHandler {

	// The log service
	private Logger log;

	// Real Service
	private ServiceProvider service;

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

	public ServiceProxy(ServiceProvider service) {
		this.service = service;
		this.log = Logger.getLogger(getClass().getSimpleName());
		try {
			m_hashCodeMethod = Object.class.getMethod("hashCode", null);
			m_equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
			m_toStringMethod = Object.class.getMethod("toString", null);
		} catch (NoSuchMethodException e) {
			throw new NoSuchMethodError(e.getMessage());
		}
	}

	/**
	 * Invocation Handler delegating invocation on the service object.
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
		InvocationEvent ie = null;
		long requestTime = System.nanoTime(), responseTime;
		Object result = null;
		boolean success = true;
		try {
			result = method.invoke(service.getServiceObject(), args);
		} catch (Exception exc) {
			success = false;
			throw exc;
		} finally {
			responseTime = System.nanoTime();
			notifyInvocation(new InvocationEvent(null, service.getServicePid(),
					method.getName(), success, requestTime, responseTime));
		}
		return result;
	}

	private void notifyInvocation(InvocationEvent invocation) {
		
	}
}
