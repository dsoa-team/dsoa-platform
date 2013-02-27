package br.ufpe.cin.dsoa.management.hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.event.InvocationEvent;

public class ServiceProxy implements InvocationHandler {

	private ServiceReference reference;
	private BundleContext context;
	private String serviceName;
	private EventProcessingCenter epCenter;

	public ServiceProxy(BundleContext context, EventProcessingCenter epCenter,
			ServiceReference reference) {
		this.context = context;
		this.reference = reference;
		this.epCenter = epCenter;
		this.serviceName = (String) reference
				.getProperty(Constants.SERVICE_PID);
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		long startTime = System.currentTimeMillis();
		Object result = null;
		Exception exception = null;
		InvocationEvent invocation = null;
		boolean success = false;
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

		invocation = new InvocationEvent(serviceName, method.getName(),
				success, startTime, System.currentTimeMillis());
		notifyInvocation(invocation);
		if (null != exception) {
			throw exception;
		}
		return result;
	}

	private void notifyInvocation(InvocationEvent invocation) {
		epCenter.publishEvent(invocation);
	}
}
