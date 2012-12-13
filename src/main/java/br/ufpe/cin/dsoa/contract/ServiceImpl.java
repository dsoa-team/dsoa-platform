package br.ufpe.cin.dsoa.contract;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;

public class ServiceImpl implements Service {

	private String id;
	private String address;
	private ServiceProvider provider;
	private Object serviceObject;
	private ServiceReference reference;
	private ServiceMetadata metadata;
	
	public ServiceImpl(ServiceReference reference) {
		this.reference = reference;
		this.serviceObject = reference.getBundle().getBundleContext().getService(this.reference);
		this.metadata = defineMetadata();
	}
	
	private ServiceMetadata defineMetadata() {
		Map<String, Object> metadata = new HashMap<String, Object>();
		for (String key : this.reference.getPropertyKeys()) {
			metadata.put(key, reference.getProperty(key));
		}
		return new ServiceMetadata(metadata);
	}
	
	public Object getServiceObject() {
		return this.serviceObject;
	}

	public ServiceMetadata getMetadata() {
		return this.metadata;
	}
	
}