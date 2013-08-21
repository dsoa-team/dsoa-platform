package br.ufpe.cin.dsoa.monitor;

import java.lang.reflect.Proxy;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.attribute.AttributeMonitor;
import br.ufpe.cin.dsoa.handler.dependency.contract.ServiceProvider;

public class MonitoredService implements Monitorable {
	private static final String REFERED_SERVICE_ID = "refered.service.id";
	private static final String REFERED_SERVICE_PID = "refered.service.pid";
	
	private Logger log;
	private MonitoredServiceMetadata metadata;
	// <target, AttributeMonitor>
	private Map<String, AttributeMonitor> metricMonitorMap;
	private boolean started;
	
	private ServiceReference reference;
	private ServiceRegistration monitorRegistration;
	private ServiceRegistration proxyRegistration;

	
	public MonitoredService(ServiceReference reference) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.reference = reference;
		this.metadata = new MonitoredServiceMetadata(reference);
		this.metricMonitorMap = new HashMap<String, AttributeMonitor>();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void start() {
		registerMonitor();
		registerProxy();
		this.started = true;
	}
	
	public void stop() {
		this.monitorRegistration.unregister();
		this.proxyRegistration.unregister();
		this.started = false;
	}
	
	@SuppressWarnings("rawtypes")
	public void registerMonitor() {
		log.info("Registering monitor...");
		Hashtable ht = new Hashtable();
		ht.put(Constants.SERVICE_PID, getMonitoredServicePid());
		ht.put(REFERED_SERVICE_ID, metadata.getId());
		ht.put(REFERED_SERVICE_PID, metadata.getPid());
		String[] clazzes = {Monitorable.class.getName()};
		this.monitorRegistration = this.reference.getBundle().getBundleContext().registerService(clazzes, this, ht);
	}
	
	@SuppressWarnings("rawtypes")
	private void registerProxy() {
		log.info("Registering proxy...");
		ClassLoader cl = this.getClass().getClassLoader();

		log.info("A new remote service was registered: "
				+ reference.getProperty("service.id"));
		log.info("Creating a service proxy...");
		String[] classNames = (String[]) reference
				.getProperty(Constants.OBJECTCLASS);
		Class<?>[] classes = new Class<?>[classNames.length];
		int i = 0;
		for (String clazz : classNames) {
			try {
				classes[i++] = cl.loadClass(clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		Dictionary dict = copyProperties(reference);
		Object proxy = Proxy.newProxyInstance(cl, classes,
				new MonitoredServiceProxy(new ServiceProvider(reference.getBundle().getBundleContext(), reference)));
		this.proxyRegistration = this.reference.getBundle().getBundleContext().registerService(classNames, proxy, dict);
	}
	
	private Dictionary copyProperties(ServiceReference reference) {
		String[] keys = reference.getPropertyKeys();
		Dictionary dict = new Hashtable();
		for (String key : keys) {
			if (!key.equals("service.managed")) {
				dict.put(key, reference.getProperty(key));
			}
		}
		dict.put(br.ufpe.cin.dsoa.util.Constants.SERVICE_PROXY, "true");
		return dict;
	}
	
	public void addMetricMonitor(AttributeMonitor monitor) {
		this.metricMonitorMap.put(monitor.getStatusVariableId(), monitor);
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
			return metricMonitorMap.get(id).getAttributeDescription();
		}
		throw new IllegalArgumentException("Variable " + id + " does not exist");
	}

	public Map<String, AttributeMonitor> getMetricVariableMap() {
		return this.metricMonitorMap;
	}

	public MonitoredServiceMetadata getMetadata() {
		return this.metadata;
	}
}
