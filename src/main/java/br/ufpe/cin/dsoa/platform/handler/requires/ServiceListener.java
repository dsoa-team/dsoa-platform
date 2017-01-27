package br.ufpe.cin.dsoa.platform.handler.requires;

import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public interface ServiceListener {

	void onArrival(ServiceInstance service);
	
	void onDeparture(ServiceInstance service);
	
	void onError(Exception e);

}
