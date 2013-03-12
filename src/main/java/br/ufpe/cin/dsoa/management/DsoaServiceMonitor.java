package br.ufpe.cin.dsoa.management;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.monitor.MonitoringConfiguration;
import br.ufpe.cin.dsoa.monitor.MonitoringConfigurationItem;

public class DsoaServiceMonitor implements Monitorable {

	private ServiceReference reference;
	private MonitoringConfiguration config;
	private MetricCatalog metricCatalog;
	private EventProcessingCenter epCenter;
	private Map<String, MetricMonitor> metricVariableMap;

	public DsoaServiceMonitor(EventProcessingCenter epCenter, MetricCatalog metricCatalog, ServiceReference reference) {
		this.epCenter = epCenter;
		this.metricCatalog = metricCatalog;
		this.reference = reference;
		this.metricVariableMap = new HashMap<String, MetricMonitor>();
		this.startMonitoring(reference);
	}

	// prefix.category.metric.target
	// metric.QoS.ResponseTime.priceAlert
	private void startMonitoring(ServiceReference reference) {
		String keys[] = reference.getPropertyKeys();
		for (String key : keys) {
			if (key.toLowerCase().startsWith(Metric.METRIC_PREFIX)) {
				MetricParser parser = new MetricParser(key.substring(Metric.METRIC_PREFIX.length()));
				Metric metric = metricCatalog.getMetric(parser.getMetricId());
				if (null != metric) {
					if (config == null) {
						config = new MonitoringConfiguration();
					}
					MonitoringConfigurationItem item;
					config.addItem(item = new MonitoringConfigurationItem(metric, parser.getTarget()));
					MetricMonitor metricMonitor = new MetricMonitor(item);
					this.metricVariableMap.put(metricMonitor.getPath(), metricMonitor);
					this.epCenter.subscribe(metric.getId().toString(), metricMonitor);
				}
			}
		}
	}

	public String[] getStatusVariableNames() {
		String[] variableNames = new String[metricVariableMap.size()];
		int i = 0;
		for (String key : metricVariableMap.keySet()) {
			variableNames[i++] = key;
		}
		return variableNames;
	}

	public StatusVariable getStatusVariable(String id) throws IllegalArgumentException {
		if (metricVariableMap.containsKey(id)) {
			return metricVariableMap.get(id).getStatusVariable();
		}
		throw new IllegalArgumentException("Variable " + id + " does not exist");
	}

	public boolean notifiesOnChange(String id) throws IllegalArgumentException {
		return false;
	}

	public boolean resetStatusVariable(String id) throws IllegalArgumentException {
		return false;
	}

	public String getDescription(String id) throws IllegalArgumentException {
		if (metricVariableMap.containsKey(id)) {
			return metricVariableMap.get(id).getDescription();
		}
		throw new IllegalArgumentException("Variable " + id + " does not exist");
	}

}
