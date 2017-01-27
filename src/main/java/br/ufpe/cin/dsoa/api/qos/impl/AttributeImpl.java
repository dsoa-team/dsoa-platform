package br.ufpe.cin.dsoa.api.qos.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.core.impl.NamedElementImpl;
import br.ufpe.cin.dsoa.api.qos.Attribute;
import br.ufpe.cin.dsoa.api.qos.Category;
import br.ufpe.cin.dsoa.api.qos.Metric;
import br.ufpe.cin.dsoa.util.Constants;

public class AttributeImpl extends NamedElementImpl implements Attribute {

	public Map<String, Metric> mapMetrics;
	private Category category;
	private String fullname;
	
	public AttributeImpl(Category category, String name) {
		super(name);
		this.category = category;
		this.fullname = category.getName() + Constants.TOKEN + name;
		this.mapMetrics = new HashMap<String, Metric>();
	}

	@Override
	public List<Metric> getMetrics() {
		return new ArrayList<Metric>(mapMetrics.values());
	}

	@Override
	public Metric getMetric(String name) {
		return this.mapMetrics.get(name);
	}
	
	public void addMetric(String metricName) {
		this.mapMetrics.put(metricName, new MetricImpl(this, metricName));
	}

	@Override
	public Category getCategory() {
		return category;
	}

	@Override
	public String getFullname() {
		return this.fullname;
	}

}
