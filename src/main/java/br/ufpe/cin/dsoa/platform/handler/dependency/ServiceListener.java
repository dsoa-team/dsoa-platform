package br.ufpe.cin.dsoa.platform.handler.dependency;

import br.ufpe.cin.dsoa.service.Service;

public interface ServiceListener {

	void onArrival(Service service);
	
	void onDeparture(Service service);

}
