package br.ufpe.cin.dsoa.platform.handler.requires;

import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public interface ServiceListener {

	String getServiceInterfaceName();
	
	void onArrival(ServiceInstance service);
	
	void onDeparture(ServiceInstance service);
	
	void onError(Exception e);

}
