package br.ufpe.cin.dsoa.monitor;

import org.osgi.framework.ServiceReference;

public interface MonitoredServiceListener {
	void onArrival(ServiceReference reference);

	void onDeparture(ServiceReference reference);
}
