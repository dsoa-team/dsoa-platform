package br.ufpe.cin.dsoa.metric.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.configurator.parser.metric.MetricList;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.metric.MetricCatalog;
import br.ufpe.cin.dsoa.metric.MetricId;

public class MetricComputingCenter implements MetricCatalog {
	private Map<MetricId,Metric> metricMap = new HashMap<MetricId,Metric>();
	private EventProcessingService epCenter;
	
	public Metric getMetric(MetricId id) {
		return this.metricMap.get(id);
	}

	public Collection<Metric> getMetrics() {
		return this.metricMap.values();
	}

	public void addMetric(Metric metric) {
		this.metricMap.put(metric.getId(), metric);
		this.epCenter.defineStatement(metric.toString(), metric.getQuery());
	}

	public void addMetrics(MetricList metrics) {
		for (Metric metric : metrics.getMetrics()) {
			this.addMetric(metric);
		}
	}
}
