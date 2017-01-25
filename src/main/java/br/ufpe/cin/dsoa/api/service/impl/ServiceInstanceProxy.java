package br.ufpe.cin.dsoa.api.service.impl;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.service.Port;
import br.ufpe.cin.dsoa.api.service.Property;

public class ServiceInstanceProxy extends PortInstanceImpl implements ServiceInstanceProxyItf {

	private Object serviceObject;
	private ServiceReference reference;
	
	/**
	 * By now, the ServiceInstance name comes from the "service.pid" ou  "service.id"
	 * @param port
	 * @param props
	 * @param reference
	 */
	public ServiceInstanceProxy(Port port, List<Property> props, ServiceReference reference) {
		super(port.getName(), port, props);
		this.reference = reference;
	}

	public ServiceReference getServiceReference() {
		return reference;
	}

	public void ungetServiceObject() {
		reference.getBundle().getBundleContext().ungetService(reference);
		this.serviceObject = null;
	}
	
	public Object getServiceObject() {
		if (serviceObject == null && 
				null != reference) {
			serviceObject = reference.getBundle().getBundleContext()
					.getService(reference);
		}
		return serviceObject;
		
	}
	
}

