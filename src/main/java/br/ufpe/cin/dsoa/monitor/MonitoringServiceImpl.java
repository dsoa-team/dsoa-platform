package br.ufpe.cin.dsoa.monitor;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.management.ManagedService;
import br.ufpe.cin.dsoa.metric.MetricCatalog;
import br.ufpe.cin.dsoa.metric.MetricId;
import br.ufpe.cin.dsoa.metric.MetricInstance;
import br.ufpe.cin.dsoa.metric.MetricParser;
import br.ufpe.cin.dsoa.metric.MetricStatus;

public class MonitoringServiceImpl implements MonitoringService {

	private MetricCatalog metricCatalog;
	private EventProcessingService epCenter;
	
	public void startMonitoring(ManagedService mgrService) {
		parseMetrics(mgrService);
	}
	
	public void stopMonitoring(ManagedService mgrService) {
		mgrService.getStatusVariableNames();
	}

	private void parseMetrics(ManagedService mgrService) {
		// prefix.category.metric.target
		// metric.QoS.ResponseTime.priceAlert
		ServiceReference reference = mgrService.getServiceReference();
		String keys[] = reference.getPropertyKeys();
		for (String key : keys) {
			if (key.toLowerCase().startsWith(Metric.METRIC_PREFIX)) {
				MetricParser parser = new MetricParser(key.substring(Metric.METRIC_PREFIX.length()));
				String operation = parser.getOperationName();
				MetricId metricId = parser.getMetricId();
				this.setupMetricMonitor(metricId, mgrService, operation);
			}
		}
	}
	
	public void setupMetricMonitor(MetricId metricId, ManagedService service, String operation) {
		Metric metric = metricCatalog.getMetric(metricId);
		if (null != metric) {
			MetricInstance metricInstance = new MetricInstance(metric, service.getPid(), operation);
			String target = metricInstance.getTarget();
			MetricStatus status = new MetricStatus(metricInstance);
			epCenter.defineStatement(target, metric.getQuery());
			epCenter.subscribe(target, status);
			service.addStatusVariable(status);
		}
	}

}
