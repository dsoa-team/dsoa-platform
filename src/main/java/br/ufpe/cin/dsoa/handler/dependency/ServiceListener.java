package br.ufpe.cin.dsoa.handler.dependency;

import org.osgi.framework.ServiceReference;

public interface ServiceListener {

	void notifyArrival(ServiceReference service);
	
	void notifyDeparture(ServiceReference service);

}
