package br.ufpe.cin.dsoa.management;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.monitor.Monitorable;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.event.InvocationEvent;

/**
 * A Service Proxy that intercepts requests at the client side. It generates events that represent service invocation (InvocationEvent) and forwards them
 * to the Event Processing Center. There some Metric Computing Agents are responsible for metric derivation.
 * @author fabions
 *
 */
public class DsoaServiceProxy implements InvocationHandler {
	
	/*
	 * DSOA BundleContext
	 */
	private BundleContext context;

	/*
	 * The Event Processing Center component
	 */
	private EventProcessingCenter epCenter;

	/*
	 * The log service
	 */
	private Logger log;

	/*
	 * The reference to the real service
	 */
	private ServiceReference reference;

	
	public DsoaServiceProxy(BundleContext context, 
			EventProcessingCenter epCenter,
			ServiceReference reference) {
		this.context = context;
		this.epCenter = epCenter;
		this.reference = reference;
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
		String serviceName = reference.getProperty(Constants.SERVICE_ID).toString();
		Object service = context.getService(reference);
		try {
			if (null != service) {
				result = method.invoke(service, args);
				success = true;
			} else {
				throw new IllegalStateException(
						"Required service is not available.");
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
		epCenter.publishEvent(invocation);
	}
}
