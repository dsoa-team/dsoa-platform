package br.ufpe.cin.dsoa.management;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.monitor.MonitoredServiceMetadata;

public interface ManagementService {

	void onArrival(ServiceReference reference);

	void onDeparture(ServiceReference reference);

	List<String> getMetricList();
	
	void addMetric(String category, String metric, String servicePid, String operationName);
	
	void addMetricMonitor(String servicePid, String metricName, String metricCategory, String operationName);
	
	public MonitoredServiceMetadata getManagedServiceMetadata(String id);
	
	public List<MonitoredServiceMetadata> getManagedServicesMetadata();
}