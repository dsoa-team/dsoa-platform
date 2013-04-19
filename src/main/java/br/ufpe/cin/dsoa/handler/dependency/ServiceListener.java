package br.ufpe.cin.dsoa.handler.dependency;

import org.osgi.framework.ServiceReference;

public interface ServiceListener {

	void onArrival(ServiceReference service);
	
	void onDeparture(ServiceReference service);

}
