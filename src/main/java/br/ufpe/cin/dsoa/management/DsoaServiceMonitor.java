package br.ufpe.cin.dsoa.management;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.monitor.MonitoringConfiguration;
import br.ufpe.cin.dsoa.monitor.MetricInstance;

public class DsoaServiceMonitor implements Monitorable, ServiceMetricManager {
	
	private String serviceId;
	private ServiceReference reference;
	private MetricCatalog metricCatalog;
	private EventProcessingCenter epCenter;
	private Map<String, MetricMonitor> metricVariableMap;


	public DsoaServiceMonitor(EventProcessingCenter epCenter, MetricCatalog metricCatalog, ServiceReference reference) {
		this.epCenter = epCenter;
		this.metricCatalog = metricCatalog;
		this.reference = reference;
		this.serviceId = reference.getProperty(Constants.SERVICE_ID).toString();
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
					MetricInstance metricInstance = new MetricInstance(metric, serviceId, parser.getOperationName());
					this.setupMetricMonitor(metricInstance);
				}
			}
		}
	}

	@Override
	public void addMetric(MetricId metricId, String serviceId,
			String operationName) {
		
		Metric metric = metricCatalog.getMetric(metricId);
		if(null !=  metric){
			MetricInstance metricInstance = new MetricInstance(metric, serviceId, operationName);
			this.setupMetricMonitor(metricInstance);
		}
		
	}
	
	/**
	 * Creates a metricInstance and subscribe a query on complex event process
	 * engine through EventProcessingCenter component.
	 * 
	 * @param metricInstance
	 */
	private void setupMetricMonitor(MetricInstance metricInstance) {
		MetricMonitor metricMonitor = new MetricMonitor(metricInstance);
		if(!this.metricVariableMap.containsKey(metricInstance.getTarget())){
			this.metricVariableMap.put(metricInstance.getTarget(), metricMonitor);
			this.epCenter.subscribe(metricInstance.getMetric().toString(), metricMonitor);
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
