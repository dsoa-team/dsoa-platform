package br.ufpe.cin.dsoa.management;

public class StochasticVariableTemplate {


	private String target;
	private MetricId metricId;

	public StochasticVariableTemplate(MetricId metricId, String target) {
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
