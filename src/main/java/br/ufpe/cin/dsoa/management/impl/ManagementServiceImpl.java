package br.ufpe.cin.dsoa.management.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.management.ManagementService;
import br.ufpe.cin.dsoa.metric.MetricComputingService;
import br.ufpe.cin.dsoa.metric.MetricId;
import br.ufpe.cin.dsoa.metric.MetricInstance;
import br.ufpe.cin.dsoa.metric.MetricInstanceImpl;
import br.ufpe.cin.dsoa.monitor.MonitoredService;
import br.ufpe.cin.dsoa.monitor.MonitoredServiceMetadata;
import br.ufpe.cin.dsoa.monitor.MonitoringService;

public class ManagementServiceImpl implements ManagementService {

	private MetricComputingService metricComputingService;
	private MonitoringService monitoringService;
	
	@Override
	public synchronized void onArrival(ServiceReference reference) {
		List<MetricInstance> metricInstances = metricComputingService.getMetricInstances(reference);
		monitoringService.startMonitoring(reference, metricInstances);
	}
	@Override
	public synchronized void onDeparture(ServiceReference reference) {
		monitoringService.stopMonitoring(reference);
	}

	public List<MonitoredServiceMetadata> getManagedServicesMetadata() {
		List<MonitoredServiceMetadata> metadata = new ArrayList<MonitoredServiceMetadata>();
		for (MonitoredService monitoredService : this.monitoringService.getMonitoredServices()) {
			metadata.add(monitoredService.getMetadata());
		}
		return metadata;
	}
	
	public MonitoredServiceMetadata getManagedServiceMetadata(String id){
		MonitoredService service = this.monitoringService.getMonitoredService(id);
		MonitoredServiceMetadata metadata = null;
		if(null != service) {
			metadata = service.getMetadata();
		}
		
		return metadata;
	}

	@Override
	public List<String> getMetricList() {
		List<String> metricList = new ArrayList<String>();
		
		for(Metric m : this.metricComputingService.getMetrics()){
			metricList.add(m.toString());
		}
		return metricList;
	}
	
	@Override
	public void addMetric(String category, String name, String servicePid, String operationName) {
		MetricId id = new MetricId(category, name);
		Metric metric = this.metricComputingService.getMetric(id);
		MetricInstance metricInstance = new MetricInstanceImpl(metric, servicePid, operationName);
		this.monitoringService.addMetric(servicePid, metricInstance);
	}
	
	@Override
	public void addMetricMonitor(String servicePid, String metricName, String metricCategory, String operationName) {
		Metric metric = this.metricComputingService.getMetric(new MetricId(metricCategory, metricName));
		MetricInstance metricInstance = new MetricInstanceImpl(metric, servicePid, operationName);
		this.monitoringService.addMetric(servicePid, metricInstance);
	}
}
