package br.ufpe.cin.dsoa.metric;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;

public interface MetricInstance {
	Metric getMetric();
	String getTarget();
	String getServiceId();
	String getOperationName();
	String getCategory();
	String getName();
}
