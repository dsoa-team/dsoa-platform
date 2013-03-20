package br.ufpe.cin.dsoa.management;

import br.ufpe.cin.dsoa.management.metric.MetricId;

public class Slo {


	private String target;
	private MetricId metricId;

	public Slo(MetricId metricId, String target) {
		this.metricId = metricId;
		this.target = target;
	}

	public String getTarget() {
		return target;
	}
	
	public MetricId getMetricId() {
		return metricId;
	}
}
