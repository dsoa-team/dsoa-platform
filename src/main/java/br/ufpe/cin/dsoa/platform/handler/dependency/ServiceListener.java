package br.ufpe.cin.dsoa.platform.handler.dependency;

import br.ufpe.cin.dsoa.service.Service;

public interface ServiceListener {

	String getServiceInterface();
	
	void onArrival(Service service);
	
	void onDeparture(Service service);
	
	void onError(Exception e);

}
