package br.ufpe.cin.dsoa.api.service.impl;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public interface ServiceInstanceProxy extends ServiceInstance {
	public Object getServiceObject();

	public void ungetServiceObject();
	
	public ServiceReference getServiceReference();
}
