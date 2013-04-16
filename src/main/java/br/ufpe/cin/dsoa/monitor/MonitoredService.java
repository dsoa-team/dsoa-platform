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
	private Map<String, MetricMonitor> metricMonitorMap;
	private boolean started;
	private ServiceRegistration registration;

	
	public MonitoredService(ServiceReference reference) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.metadata = new MonitoredServiceMetadata(reference);
		this.metricMonitorMap = new HashMap<String, MetricMonitor>();
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
		this.metricMonitorMap.put(monitor.getTarget(), monitor);
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
		String[] variableNames = new String[metricMonitorMap.size()];
		int i = 0;
		for (String key : metricMonitorMap.keySet()) {
			variableNames[i++] = key;
		}
		return variableNames;
	}

	public StatusVariable getStatusVariable(String id) throws IllegalArgumentException {
		if (metricMonitorMap.containsKey(id)) {
			return metricMonitorMap.get(id).getStatusVariable();
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
		if (metricMonitorMap.containsKey(id)) {
			return metricMonitorMap.get(id).getDescription();
		}
		throw new IllegalArgumentException("Variable " + id + " does not exist");
	}

	public Map<String, MetricMonitor> getMetricVariableMap() {
		return this.metricMonitorMap;
	}

	public MonitoredServiceMetadata getMetadata() {
		return this.metadata;
	}
}
