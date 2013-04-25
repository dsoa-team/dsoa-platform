package br.ufpe.cin.dsoa.handler.dependency.contract;

import org.osgi.framework.ServiceReference;

public class ServiceProvider {
	private String servicePid;
	private ServiceReference reference;
	private Object serviceObject;
	
	public ServiceProvider(String servicePid, ServiceReference reference, Object serviceObject) {
		super();
		this.servicePid = servicePid;
		this.reference = reference;
		this.serviceObject = serviceObject;
	}
	
	public String getServicePid() {
		return servicePid;
	}
	public ServiceReference getReference() {
		return reference;
	}
	public Object getServiceObject() {
		return serviceObject;
	}
}
