package br.ufpe.cin.dsoa.management;

import java.util.Collection;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.configurator.parser.metric.MetricList;

public interface MetricCatalog {

	public abstract Metric getMetric(MetricId id);

	public abstract Collection<Metric> getMetrics();

	public abstract void addMetric(Metric metric);

	public abstract void addMetrics(MetricList metrics);

}