package br.ufpe.cin.dsoa.platform.component.autonomic;

import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public interface DsoaServiceListener {

	void onArrival(ServiceInstance service);
	
	void onDeparture(ServiceInstance service);
	
	void onError(Exception e);

}
