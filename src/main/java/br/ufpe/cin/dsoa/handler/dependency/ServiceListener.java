package br.ufpe.cin.dsoa.handler.dependency;

import br.ufpe.cin.dsoa.handler.dependency.contract.ServiceProvider;

public interface ServiceListener {

	void onArrival(ServiceProvider service);
	
	void onDeparture(ServiceProvider service);

}
