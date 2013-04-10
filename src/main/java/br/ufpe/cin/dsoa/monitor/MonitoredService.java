package br.ufpe.cin.dsoa.monitor;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.metric.MetricMonitor;

public class MonitoredService implements Monitorable {
	private static final String REFERED_SERVICE_ID = "refered.service.id";
	private static final String REFERED_SERVICE_PID = "refered.service.pid";
	
	private Logger log;
	private MonitoredServiceMetadata metadata;
	// <target, MetricMonitor>
	private Map<String, MetricMonitor> metricVariableMap;
	private boolean started;
	private ServiceRegistration registration;

	
	public MonitoredService(ServiceReference reference) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.metricVariableMap = new HashMap<String, MetricMonitor>();
		this.metadata = new MonitoredServiceMetadata(reference);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void start() {
		log.info("Registering monitor...");
		Hashtable ht = new Hashtable();
		ht.put(Constants.SERVICE_PID, getMonitoredServicePid());
		ht.put(REFERED_SERVICE_ID, metadata.getId());
		ht.put(REFERED_SERVICE_PID, metadata.getPid());
		String[] clazzes = {Monitorable.class.getName()};
		this.registration = this.metadata.getReference().getBundle().getBundleContext().registerService(clazzes, this, ht);
		this.started = true;
	}
	
	public void stop() {
		this.registration.unregister();
		this.started = false;
	}

	public void addMetricMonitor(MetricMonitor monitor) {
		this.metricVariableMap.put(monitor.getTarget(), monitor);
	}
	
	private String getMonitoredServicePid() {
		return this.metadata.getPid() + "-Monitor";
	}
	
	public String getId() {
		return this.metadata.getId();
	}
	
	public String getPid() {
		return this.metadata.getPid();
	}
	
	public boolean isStarted() {
		return this.started;
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

	public Map<String, MetricMonitor> getMetricVariableMap() {
		return this.metricVariableMap;
	}

	public MonitoredServiceMetadata getMetadata() {
		return this.metadata;
	}
}
