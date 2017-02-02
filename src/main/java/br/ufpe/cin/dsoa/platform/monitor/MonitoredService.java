package br.ufpe.cin.dsoa.platform.monitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.api.event.EventProcessingService;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.api.service.impl.ServiceInstanceImpl;

public class MonitoredService implements Monitorable {
	
	
	private final static Map<Class<?>, Object> defaultValues = new HashMap<Class<?>, Object>();

	static {
		defaultValues.put(String.class, "");
		defaultValues.put(Integer.class, 0);
		defaultValues.put(int.class, 0);
		defaultValues.put(Long.class, 0L);
		defaultValues.put(long.class, 0L);
		defaultValues.put(Character.class, '\0');
		defaultValues.put(char.class, '\0');
		defaultValues.put(float.class, 0F);
		defaultValues.put(Float.class, 0F);
		defaultValues.put(double.class, 0d);
		defaultValues.put(Double.class, 0d);
	}
	
	
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
	
	private MonitoringAgent		monitoringAgent;
	private EventTypeCatalog 	eventCatalog;
	private EventProcessingService epService;
	
	public MonitoredService(BundleContext ctx, ServiceInstance service, EventTypeCatalog eventCatalog, EventProcessingService epService) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.attributeMonitorMap = new HashMap<String, MonitoredAttribute>();
		this.service = service;
		this.ctx = ctx;
		this.metadata = new ServiceMetadata(service);
		this.notifiesOnChange = true;
		this.eventCatalog = eventCatalog;
		this.epService = epService;
		this.monitoringAgent = this.createMonitoringAgent();
	}

	
	public void start() {
		if (!started) {
			// TODO Em uma versão pronta, descomentar.
			//registerMonitor();
			//registerProxy();
			if (this.monitoringAgent != null) {
				monitoringAgent.start();
			}
			this.started = true;
		}
	}
	
	public void stop() {
		if (started) {
			if (monitorRegistration != null) {
				this.monitorRegistration.unregister();
			}
			if (proxyRegistration != null) {
				this.proxyRegistration.unregister();
			}
			if (this.monitoringAgent != null) {
				monitoringAgent.stop();
			}
			this.started = false;
		}
	}
	
	
	private MonitoringAgent createMonitoringAgent() {
		String itfName = getServiceSpecification().getFunctionalInterface().getInterfaceName();
		try {
			Class<?> itfClass = this.ctx.getBundle().loadClass(itfName);
			List<Method> methods = new ArrayList<Method>();
			Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
			for (Method method : itfClass.getMethods()) {	
				methods.add(method);
				Object[] params = new Object[method.getParameterTypes().length];
				int j = 0;
				for (Class<?> paramType : method.getParameterTypes()) {
					params[j++] = defaultValues.get(paramType);
				}
				paramMap.put(method.getName(), params);
			}
			ServiceInstance serviceInstance = getServiceInstance();
			if (serviceInstance instanceof ServiceInstanceImpl) {
				ServiceReference reference = ((ServiceInstanceImpl)serviceInstance).getServiceReference();
				return new MonitoringAgent(eventCatalog, epService, ctx, service.getName(), itfName, reference, methods, paramMap);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not start Monitoring Agent for service " + service.getName());
			e.printStackTrace();
		}
		return null;
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
