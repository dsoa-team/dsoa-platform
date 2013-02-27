package br.ufpe.cin.dsoa.management.hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.event.InvocationEventOld;

public class ServiceProxy implements InvocationHandler {

	private ServiceReference reference;
	private BundleContext context;
	private String serviceName;

	public ServiceProxy(BundleContext context, ServiceReference reference) {
		this.context = context;
		this.reference = reference;
		this.serviceName = (String) reference
				.getProperty(Constants.SERVICE_PID);
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		long startTime = System.currentTimeMillis();
		Object result = null;
		Exception exception = null;
		InvocationEventOld invocation = null;
		boolean success = false;
		Object service = context.getService(reference);
		try {
			if(null != service) {
				result =  method.invoke(service, args);
				success = true;
			} else {
				throw new IllegalStateException("Required service is not available.");
			}
		} catch(Exception exc) {
			exception = exc;
		}
		
		invocation = new InvocationEventOld(serviceName, 
				method.getName(), method.getParameterTypes(), args, 
				method.getReturnType(), result, success, exception,
				startTime,
				System.currentTimeMillis());
		notifyInvocation(invocation);
		if (null == exception) {
			throw exception;
		}
		return result;
	}

	private void notifyInvocation(InvocationEventOld invocation) {
		
	}
}
