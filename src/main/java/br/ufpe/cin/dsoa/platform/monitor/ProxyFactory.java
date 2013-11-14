package br.ufpe.cin.dsoa.platform.monitor;

import br.ufpe.cin.dsoa.api.service.Service;

public interface ProxyFactory {

	public Object getProxy(String consumerId, Service service);
}
