package br.ufpe.cin.dsoa.metric;

import java.util.Collection;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.configurator.parser.metric.MetricList;

public interface MetricCatalog {

	public Metric getMetric(MetricId id);

	public Collection<Metric> getMetrics();

	public void addMetric(Metric metric);

	public void addMetrics(MetricList metrics);

}