package br.ufpe.cin.dsoa.api.service.impl;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.service.DsoaComponentInstance;
import br.ufpe.cin.dsoa.api.service.Port;
import br.ufpe.cin.dsoa.api.service.Property;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public class ServiceInstanceImpl extends PortInstanceImpl implements ServiceInstance {

	private Object serviceObject;
	private ServiceReference reference;
	
	/**
	 * The corresponding serviceInstance
	 */
	private DsoaComponentInstance componentInstance;
	
	// TODO NOT USED BY NOW! AJUST THIS IMPLEMENTTATION WHEN THE PROVIDER HANDLER IS READY!!!
	public ServiceInstanceImpl(DsoaComponentInstance componentInstance, Port port, List<Property> props) {
		super(componentInstance.getName()+"-"+port.getName(), port, props);
		this.componentInstance = componentInstance;
		this.componentInstance.addServiceInstance(this);
	}

	/**
	 * By now, the ServiceInstance name comes from the "service.pid" ou  "service.id"
	 * @param port
	 * @param props
	 * @param reference
	 */
	public ServiceInstanceImpl(Port port, List<Property> props, ServiceReference reference) {
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
