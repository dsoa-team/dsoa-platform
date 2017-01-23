package br.ufpe.cin.dsoa.platform.monitor;

import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public interface ProxyFactory {

	public Object getProxy(String consumerId, ServiceInstance service);
}
