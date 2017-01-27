package br.ufpe.cin.dsoa.api.qos;

import java.util.List;

import br.ufpe.cin.dsoa.api.core.NamedElement;

public interface Attribute extends NamedElement {
	public List<Metric> getMetrics();
	public Metric getMetric(String metName);
	
	public Category getCategory();
	
	public String getFullname();
}
