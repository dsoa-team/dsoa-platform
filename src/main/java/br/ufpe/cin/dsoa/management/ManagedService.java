package br.ufpe.cin.dsoa.management;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.metric.MetricStatus;

public class ManagedService implements Monitorable {

	private ManagedServiceMetadata metadata;
	private ServiceRegistration registration;
	private Map<String, MetricStatus> metricVariableMap;
	private Logger log;
	
	public ManagedService(ServiceReference reference) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.metadata = new ManagedServiceMetadata(reference);
		this.metricVariableMap = new HashMap<String, MetricStatus>();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void start() {
		log.info("Registering monitor...");
		Hashtable ht = new Hashtable();
		ht.put(Constants.SERVICE_PID, metadata.getPid() + "-Monitor");
		String[] clazzes = {Monitorable.class.getName(), ServiceMonitorConfigurator.class.getName()};
		this.registration = this.metadata.getContext().registerService(clazzes, this, ht);
	}
	

	public void stop() {
		registration.unregister();
	}
	
	public String getId() {
		return metadata.getId();
	}
	
	public String getPid() {
		return metadata.getPid();
	}
	
	public ManagedServiceMetadata getMetadata() {
		return metadata;
	}

	public void addStatusVariable(MetricStatus status) {
		metricVariableMap.put(status.getTarget(), status);
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

	public ServiceReference getServiceReference() {
		return this.metadata.getReference();
	}
}
