package br.ufpe.cin.dsoa.monitor;

import br.ufpe.cin.dsoa.management.ManagedService;
import br.ufpe.cin.dsoa.metric.MetricId;

public interface MonitoringService {

	void startMonitoring(ManagedService mgrService);
	void setupMetricMonitor(MetricId metricId, ManagedService service, String operation);
	void stopMonitoring(ManagedService mgrService);
}
