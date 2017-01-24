package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public interface ServiceInstanceProxyItf extends ServiceInstance {
	public Object getServiceObject();

	public void ungetServiceObject();
}
