package br.ufpe.cin.dsoa.api.qos.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.core.impl.NamedElementImpl;
import br.ufpe.cin.dsoa.api.qos.Metric;
import br.ufpe.cin.dsoa.api.qos.QoSLibrary;
import br.ufpe.cin.dsoa.util.Constants;

public class QoSLibraryImpl extends NamedElementImpl implements QoSLibrary {

	private Map<String,Metric> metricsMap;
	
	public QoSLibraryImpl(String name) {
		super(name);
		this.metricsMap = new HashMap<String,Metric>();
	}

	@Override
	public Metric getMetric(String catName, String attName, String metName) {
		if (catName == null || attName == null || metName == null) {
			throw new RuntimeException("Invalid metric name exception");
		}
		return metricsMap.get(catName+Constants.TOKEN+attName+Constants.TOKEN+metName);
	}
	
	public List<Metric> getMetrics() {
		return new ArrayList<Metric>(this.metricsMap.values());
	}
	
	public void addMetric(Metric metric) {
		this.metricsMap.put(metric.getFullname(), metric);
	}
	
}
