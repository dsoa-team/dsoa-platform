package br.ufpe.cin.dsoa.management;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.MetricList;

public class MetricCatalogImpl implements MetricCatalog {
	private Map<MetricId,Metric> metricMap = new HashMap<MetricId,Metric>();
	
	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.MetricCatalog#getMetric(br.ufpe.cin.dsoa.management.MetricId)
	 */
	public Metric getMetric(MetricId id) {
		return this.metricMap.get(id);
	}
	
	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.MetricCatalog#getMetrics()
	 */
	public Collection<Metric> getMetrics() {
		return this.metricMap.values();
	}
	
	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.MetricCatalog#addMetric(br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.Metric)
	 */
	public void addMetric(Metric metric) {
		this.metricMap.put(metric.getId(), metric);
	}
	
	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.MetricCatalog#addMetrics(br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.MetricList)
	 */
	public void addMetrics(MetricList metrics) {
		for(Metric metric: metrics.getMetrics()) {
			this.addMetric(metric);
		}
	}
}
