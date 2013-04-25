package br.ufpe.cin.dsoa.metric.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.configurator.parser.metric.MetricList;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.metric.MetricComputingService;
import br.ufpe.cin.dsoa.metric.MetricId;
import br.ufpe.cin.dsoa.metric.MetricInstance;
import br.ufpe.cin.dsoa.metric.MetricInstanceImpl;
import br.ufpe.cin.dsoa.metric.MetricParser;
import br.ufpe.cin.dsoa.util.Util;

public class MetricComputingServiceImpl implements MetricComputingService {
	
	private EventProcessingService eventProcessingService;
	
	private Map<MetricId,Metric> metricMap = new HashMap<MetricId,Metric>();
	
	public Metric getMetric(MetricId id) {
		return this.metricMap.get(id);
	}

	public Collection<Metric> getMetrics() {
		return this.metricMap.values();
	}

	public void addMetric(Metric metric) {
		this.metricMap.put(metric.getId(), metric);
		this.eventProcessingService.defineStatement(metric.toString(), metric.getQuery());
	}

	public void addMetrics(MetricList metrics) {
		for (Metric metric : metrics.getMetrics()) {
			this.addMetric(metric);
		}
	}
	
	public List<MetricInstance> getMetricInstances(ServiceReference reference) {
		List<MetricInstance> metricInstances = new ArrayList<MetricInstance>();
		String servicePid = Util.getPid(reference);
		// prefix.category.metric.target
		// metric.QoS.ResponseTime.priceAlert
		String keys[] = reference.getPropertyKeys();
		for (String key : keys) {
			if (key.toLowerCase().startsWith(Metric.METRIC_PREFIX)) {
				MetricParser parser = new MetricParser(key.substring(Metric.METRIC_PREFIX.length()));
				String operation = parser.getOperationName();
				MetricId metricId = parser.getMetricId();
				Metric metric = this.getMetric(metricId);
				if (null != metric) {
					MetricInstanceImpl metricInstance = new MetricInstanceImpl(metric, servicePid, operation);
					metricInstances.add(metricInstance);
				}
			}
		}
		return metricInstances;
	}
	
	/*
	 * 
	 * Metric metric = catalog.getMetric(metricId);
		if (null != metric) {
			MetricInstance metricInstance = new MetricInstance(metric, servicePid, operation);
			MetricMonitor monitor = new MetricMonitor(metricInstance);
			MonitoredService monitoredService = monitoredServiceMap.get(servicePid);
			monitoredService.addMetricMonitor(monitor);
			String target = monitor.getTarget();
			epCenter.defineStatement(target, monitor.getMetric().getQuery());
			epCenter.subscribe(target, monitor);
		}
	 * 
	 */
}
