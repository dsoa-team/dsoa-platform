package br.ufpe.cin.dsoa.api.qos;

import java.util.List;

import br.ufpe.cin.dsoa.api.core.NamedElement;


public interface QoSLibrary extends NamedElement {
	public Metric getMetric(String catName, String attName, String metName);
	public List<Metric> getMetrics();
	public void addMetric(Metric metric);
}
