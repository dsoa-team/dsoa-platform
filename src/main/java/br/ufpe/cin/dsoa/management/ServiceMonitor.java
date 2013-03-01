package br.ufpe.cin.dsoa.management;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.Metric;

public class ServiceMonitor implements Monitorable {

	private ServiceReference reference;
	private MetricCatalog metricCatalog;
	private EventProcessingCenter epCenter;
	private Map<String, MetricStatus> metricStatusMap;
	
	public ServiceMonitor(EventProcessingCenter epCenter, MetricCatalog catalog, ServiceReference reference) {
		this.epCenter = epCenter;
		this.metricCatalog = catalog;
		this.reference = reference;
		this.metricStatusMap = new HashMap<String,MetricStatus>();
		this.startMonitoring(reference);
	}

	private void startMonitoring(ServiceReference reference) {
		String keys[] = reference.getPropertyKeys();
		for (String key : keys) {
			Metric metric = metricCatalog.getMetric(key);
			if (null != metric) {
				MetricStatus metricSt = new MetricStatus(metric.getName(), metric.getDescription());
				this.metricStatusMap.put(metric.getName(), metricSt);
				this.epCenter.subscribe(metric.getAgent().getQuery(), metricSt);
			}
		}
	}

	public String[] getStatusVariableNames() {
		String[] variableNames = new String[metricStatusMap.size()];
		int i = 0;
		for (String key : metricStatusMap.keySet()) {
			variableNames[i++] = key;
		}
		return variableNames;
	}

	public StatusVariable getStatusVariable(String id)
			throws IllegalArgumentException {
		if (metricStatusMap.containsKey(id)) {
			return metricStatusMap.get(id).getStatusVariable();
		} 
		throw new IllegalArgumentException("Variable " + id + " does not exist");
	}

	public boolean notifiesOnChange(String id) throws IllegalArgumentException {
		return false;
	}

	public boolean resetStatusVariable(String id)
			throws IllegalArgumentException {
		return false;
	}

	public String getDescription(String id) throws IllegalArgumentException {
		if (metricStatusMap.containsKey(id)) {
			return metricStatusMap.get(id).getDescription();
		} 
		throw new IllegalArgumentException("Variable " + id + " does not exist");
	}

}
