package br.ufpe.cin.dsoa.contract;

import org.osgi.framework.ServiceReference;

public class ServiceImpl implements Service {

	private Object serviceObject;
	private ServiceReference reference;
	private ServiceMetadata metadata;
	
	public ServiceImpl(ServiceReference reference, ServiceMetadata metadata) {
		this.reference = reference;
		this.serviceObject = reference.getBundle().getBundleContext().getService(this.reference);
		this.metadata = metadata;
	}
	
	public Object getServiceObject() {
		return this.serviceObject;
	}

	public ServiceMetadata getMetadata() {
		return this.metadata;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getServiceInterface() {
		// TODO Auto-generated method stub
		return null;
	}
	
}