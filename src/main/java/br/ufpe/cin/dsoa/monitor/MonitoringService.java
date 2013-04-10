package br.ufpe.cin.dsoa.monitor;

import java.util.List;

import org.osgi.framework.ServiceReference;


public interface MonitoringService {

	void startMonitoring(ServiceReference reference);
	void stopMonitoring(ServiceReference reference);
	List<MonitoredService> getMonitoredServices();
	MonitoredService getMonitoredService(String id);
}
