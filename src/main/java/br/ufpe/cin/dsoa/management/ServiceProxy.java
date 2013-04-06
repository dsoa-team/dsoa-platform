package br.ufpe.cin.dsoa.management;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.event.InvocationEvent;

/**
 * A Service Proxy that intercepts requests at the client side. It generates events that represent service invocation (InvocationEvent) and forwards them
 * to the Event Processing Center. There some Metric Computing Agents are responsible for metric derivation.
 * @author fabions
 *
 */
/*public class ServiceProxy implements InvocationHandler {
	
	
	 * The log service
	 
	private Logger log;

	
	 * The managedService
	 
	private ManagedService managedService;
	
	public ServiceProxy(ManagedService service) {
		this.managedService = service;
		this.log = Logger.getLogger(getClass().getSimpleName());
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		log.info("Intercepted request to " + method.getName());
		long startTime = System.currentTimeMillis();
		Object result = null;
		Exception exception = null;
		InvocationEvent invocation = null;
		boolean success = false;
		Object service = managedService.getService();
		try {
			if (null != service) {
				result = method.invoke(service, args);
				success = true;
			} else {
				throw new IllegalStateException(
						"The required service is not available.");
			}
		} catch (Exception exc) {
			exception = exc;
		}

		log.info("Creating an InvocationEvent...");
		invocation = new InvocationEvent(serviceName, serviceName, method.getName(),
				success, startTime, System.currentTimeMillis());
		notifyInvocation(invocation);
		
		log.info("Leaving the service proxy...");
		if (null != exception) {
			throw exception;
		}
		return result;
	}

	private void notifyInvocation(InvocationEvent invocation) {
		
		//epCenter.publishEvent(invocation);
	}
}*/
