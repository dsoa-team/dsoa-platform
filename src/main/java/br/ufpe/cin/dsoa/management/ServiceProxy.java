package br.ufpe.cin.dsoa.management;

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
	private Object service;
	private EventProcessingCenter epCenter;

	public ServiceProxy(BundleContext context, EventProcessingCenter epCenter,
			ServiceReference reference) {
		this.reference = reference;
		this.epCenter = epCenter;
		this.serviceName = (String) reference
				.getProperty(Constants.SERVICE_PID);
		this.service = context.getService(reference);
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		System.out.println("===> ESTOU NO PROXY...");
		long startTime = System.currentTimeMillis();
		Object result = null;
		Exception exception = null;
		InvocationEvent invocation = null;
		boolean success = false;
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
		System.out.println("===> SAINDO DO PROXY...");
		if (null != exception) {
			throw exception;
		}
		return result;
	}

	private void notifyInvocation(InvocationEvent invocation) {
		epCenter.publishEvent(invocation);
	}
}
