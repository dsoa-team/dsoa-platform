package br.ufpe.cin.dsoa.monitor;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.metric.MetricInstance;


public interface MonitoringService {
	void startMonitoring(ServiceReference reference, List<MetricInstance> metricInstances);
	void stopMonitoring(ServiceReference reference);
	List<MonitoredService> getMonitoredServices();
	MonitoredService getMonitoredService(String id);
	void addMetric(String servicePid, MetricInstance metricInstance);
	
}
