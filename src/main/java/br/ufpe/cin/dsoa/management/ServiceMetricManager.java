package br.ufpe.cin.dsoa.management;

public interface ServiceMetricManager {

	public void addMetric(MetricId metricId, String serviceId, String operationName);
}
