package br.ufpe.cin.dsoa.management;

import br.ufpe.cin.dsoa.metric.MetricId;

public interface ServiceMonitorConfigurator {

	public void addMetric(MetricId metricId, String serviceId, String operationName);
}
