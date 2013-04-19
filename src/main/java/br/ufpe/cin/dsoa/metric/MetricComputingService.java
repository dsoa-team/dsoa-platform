package br.ufpe.cin.dsoa.metric;

import java.util.Collection;
import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.configurator.parser.metric.MetricList;

public interface MetricComputingService {

	public Metric getMetric(MetricId id);

	public Collection<Metric> getMetrics();

	public void addMetric(Metric metric);

	public void addMetrics(MetricList metrics);

	public List<MetricInstance> getMetricInstances(ServiceReference reference);

}