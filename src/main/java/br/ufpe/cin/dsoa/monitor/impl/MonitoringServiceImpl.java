package br.ufpe.cin.dsoa.monitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.metric.MetricInstance;
import br.ufpe.cin.dsoa.metric.MetricMonitor;
import br.ufpe.cin.dsoa.monitor.MonitoredService;
import br.ufpe.cin.dsoa.monitor.MonitoringService;
import br.ufpe.cin.dsoa.util.Util;

public class MonitoringServiceImpl implements MonitoringService {

	private EventProcessingService eventProcessingService;
	
	private Map<String, MonitoredService> monitoredServiceMap = new HashMap<String, MonitoredService>();
	
	
	public void startMonitoring(ServiceReference reference, List<MetricInstance> metricInstances) {
		MonitoredService monitoredService = new MonitoredService(reference);
		for (MetricInstance metricInstance : metricInstances) {
			this.addMetricMonitor(monitoredService, metricInstance);
		}
		monitoredServiceMap.put(monitoredService.getPid(), monitoredService);
		monitoredService.start();
	}
	
	public void stopMonitoring(ServiceReference reference) {
		MonitoredService monitoredService = monitoredServiceMap.get(Util.getPid(reference));
		monitoredService.stop();
	}
	
	public void addMetric(String servicePid, MetricInstance metricInstance) {
		this.addMetricMonitor(monitoredServiceMap.get(servicePid), metricInstance);
	}
	
	
	public List<MonitoredService> getMonitoredServices(){
		List<MonitoredService> services = new ArrayList<MonitoredService>(this.monitoredServiceMap.values());
		return services;
	}
	
	public MonitoredService getMonitoredService(String id){
		MonitoredService services = this.monitoredServiceMap.get(id);
		return services;
	}
	
	private void addMetricMonitor(MonitoredService monitoredService, MetricInstance metricInstance) {
		MetricMonitor monitor = new MetricMonitor(metricInstance);
		monitoredService.addMetricMonitor(monitor);
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(metricInstance.getServiceId());
		parameters.add(metricInstance.getOperationName());
		eventProcessingService.subscribe(metricInstance.getMetric().toString(), parameters, monitor);
	}
}
