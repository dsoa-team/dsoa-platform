package br.ufpe.cin.dsoa.monitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.metric.MetricCatalog;
import br.ufpe.cin.dsoa.metric.MetricId;
import br.ufpe.cin.dsoa.metric.MetricInstance;
import br.ufpe.cin.dsoa.metric.MetricMonitor;
import br.ufpe.cin.dsoa.metric.MetricParser;
import br.ufpe.cin.dsoa.monitor.MonitoredService;
import br.ufpe.cin.dsoa.monitor.MonitoringService;
import br.ufpe.cin.dsoa.util.Util;

public class MonitoringServiceImpl implements MonitoringService {

	private EventProcessingService epCenter;
	private MetricCatalog catalog;
	
	private Map<String, MonitoredService> monitoredServiceMap = new HashMap<String, MonitoredService>();
	
	public void startMonitoring(ServiceReference reference) {
		MonitoredService monitoredService = new MonitoredService(reference);
		monitoredServiceMap.put(monitoredService.getPid(), monitoredService);
		// prefix.category.metric.target
		// metric.QoS.ResponseTime.priceAlert
		String keys[] = reference.getPropertyKeys();
		for (String key : keys) {
			if (key.toLowerCase().startsWith(Metric.METRIC_PREFIX)) {
				MetricParser parser = new MetricParser(key.substring(Metric.METRIC_PREFIX.length()));
				String operation = parser.getOperationName();
				MetricId metricId = parser.getMetricId();
				this.addMetric(metricId, monitoredService.getPid(), operation);
			}
		}
		monitoredService.start();
	}
	
	public void stopMonitoring(ServiceReference reference) {
		MonitoredService monitoredService = monitoredServiceMap.get(Util.getPid(reference));
		monitoredService.stop();
	}
	
	public void addMetric(MetricId metricId, String servicePid, String operation) {
		Metric metric = catalog.getMetric(metricId);
		if (null != metric) {
			MetricInstance metricInstance = new MetricInstance(metric, servicePid, operation);
			MetricMonitor monitor = new MetricMonitor(metricInstance);
			MonitoredService monitoredService = monitoredServiceMap.get(servicePid);
			monitoredService.addMetricMonitor(monitor);
			String target = monitor.getTarget();
			epCenter.defineStatement(target, monitor.getMetric().getQuery());
			epCenter.subscribe(target, monitor);
		}
	}
	
	public List<MonitoredService> getMonitoredServices(){
		List<MonitoredService> services = new ArrayList<MonitoredService>(this.monitoredServiceMap.values());
		
		return services;
	}
	
	public MonitoredService getMonitoredService(String id){
		MonitoredService services = this.monitoredServiceMap.get(id);
		
		return services;
	}
	
}
