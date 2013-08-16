package br.ufpe.cin.dsoa.handler.dependency.contract;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class ServiceProvider {
	private String servicePid;
	private ServiceReference reference;
	private Object serviceObject;
	private BundleContext context;
	
	public ServiceProvider(BundleContext context, ServiceReference reference) {
		super();
		this.context = context;
		this.reference = reference;
	}
	
	public String getServicePid() {
		if (servicePid == null) {
			servicePid = (String)reference.getProperty(Constants.SERVICE_PID);
		}
		return servicePid;
	}
	public ServiceReference getReference() {
		return reference;
	}
	public Object getServiceObject() {
		if (serviceObject == null) {
			serviceObject = context.getService(reference);
		}
		return serviceObject;
	}
}
