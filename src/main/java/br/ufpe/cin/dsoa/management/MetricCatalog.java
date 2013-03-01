package br.ufpe.cin.dsoa.management;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.MetricList;

public class MetricCatalog {
	private Map<String,Metric> metricMap = new HashMap<String,Metric>();
	
	public Metric getMetric(String name) {
		return this.metricMap.get(name);
	}
	
	public Collection<Metric> getMetrics() {
		return this.metricMap.values();
	}
	
	public void addMetric(Metric metric) {
		this.metricMap.put(metric.getName(), metric);
	}
	
	public void addMetrics(MetricList metrics) {
		for(Metric metric: metrics.getMetrics()) {
			this.addMetric(metric);
		}
	}
}
