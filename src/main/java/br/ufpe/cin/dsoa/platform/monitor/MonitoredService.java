package br.ufpe.cin.dsoa.platform.monitor;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.api.service.impl.ServiceSpecification;

public class MonitoredService implements Monitorable {
	
	private static final String REFERED_SERVICE_ID = "refered.service.id";
	
	private Logger log;
	
	// <target, MonitoredAttribute>
	private Map<String, MonitoredAttribute> attributeMonitorMap;
	
	private ServiceInstance service;
	
	private BundleContext ctx;
	
	private ServiceRegistration monitorRegistration;
	
	private ServiceRegistration proxyRegistration;
	
	private boolean started;
	
	private boolean notifiesOnChange;

	private ServiceMetadata metadata;
	
	public MonitoredService(BundleContext ctx, ServiceInstance service) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.attributeMonitorMap = new HashMap<String, MonitoredAttribute>();
		this.service = service;
		this.ctx = ctx;
		this.metadata = new ServiceMetadata(service);
		this.notifiesOnChange = true;
	}
	
	public void start() {
		registerMonitor();
		//registerProxy();
		this.started = true;
	}
	
	public void stop() {
		if (monitorRegistration != null) {
			this.monitorRegistration.unregister();
		}
		if (proxyRegistration != null) {
			this.proxyRegistration.unregister();
		}
		this.started = false;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void registerMonitor() {
		log.info("Registering monitor...");
		Hashtable ht = new Hashtable();
		ht.put(Constants.SERVICE_PID, getMonitoredServicePid());
		ht.put(REFERED_SERVICE_ID, service.getName());
		String[] clazzes = {Monitorable.class.getName()};
		this.monitorRegistration = this.ctx.registerService(clazzes, this, ht);
	}
	
	public synchronized void addMonitoredAttribute(MonitoredAttribute monitor) {
		this.attributeMonitorMap.put(monitor.getStatusVariableId(), monitor);
	}
	
	public String getMonitoredServicePid() {
		return this.service.getName() + "-m";
	}
	
	public String getComponentId() {
		return this.service.getName();
	}
	
	public ServiceMetadata getMetadata() {
		return metadata;
	}

	public boolean isStarted() {
		return this.started;
	}
	
	public ServiceInstance getServiceInstance() {
		return this.service;
	}
	
	public ServiceSpecification getServiceSpecification(){
		return this.service.getPort().getServiceSpecification();
	}
	
	public String[] getStatusVariableNames() {
		String[] variableNames = new String[attributeMonitorMap.size()];
		int i = 0;
		for (String key : attributeMonitorMap.keySet()) {
			variableNames[i++] = key;
		}
		return variableNames;
	}

	public StatusVariable getStatusVariable(String id) throws IllegalArgumentException {
		if (attributeMonitorMap.containsKey(id)) {
			return attributeMonitorMap.get(id).getStatusVariable();
		}
		throw new IllegalArgumentException("Variable " + id + " does not exist");
	}

	public boolean notifiesOnChange(String id) throws IllegalArgumentException {
		return this.notifiesOnChange;
	}

	public boolean resetStatusVariable(String id) throws IllegalArgumentException {
		return false;
	}

	public String getDescription(String id) throws IllegalArgumentException {
		if (attributeMonitorMap.containsKey(id)) {
			return attributeMonitorMap.get(id).getAttributeDescription();
		}
		throw new IllegalArgumentException("Variable " + id + " does not exist");
	}

	public Map<String, MonitoredAttribute> getMetricVariableMap() {
		return this.attributeMonitorMap;
	}

	public boolean isNotifiesOnChange() {
		return notifiesOnChange;
	}

	public void setNotifiesOnChange(boolean notifiesOnChange) {
		this.notifiesOnChange = notifiesOnChange;
	}

}
