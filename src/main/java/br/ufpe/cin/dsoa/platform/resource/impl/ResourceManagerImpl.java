package br.ufpe.cin.dsoa.platform.resource.impl;

import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;

import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.api.event.EventProcessingService;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.platform.management.ManagementInfomationBase;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredAttribute;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.platform.resource.ManagedService;
import br.ufpe.cin.dsoa.platform.resource.ResourceManager;
import br.ufpe.cin.dsoa.platform.resource.mbean.ManagedAgent;

/**
 * This class was defined in order to expose Event Processing Agents via JMX.
 * Every time that a new Event Processing Agent is created, a MBean is registered
 * in order to enable its dynamic alteration.
 * 
 * @author fabions
 *
 */
public class ResourceManagerImpl implements ResourceManager {

	private MonitoringService monitoringService;
	private ManagementInfomationBase managementInfomationBase;
	private EventProcessingService epService;
	//private ProxyFactory proxyFactory;
	
	private Map<String, ManagedServiceImpl> managedServices = new HashMap<String, ManagedServiceImpl>();
	private Map<String, ManagedAgent> managedAgents = new HashMap<String, ManagedAgent>();

	public synchronized ManagedService manage(ServiceInstance service) {
		ManagedServiceImpl managedService = new ManagedServiceImpl(service);
		managedService.start();
		
		this.managedServices.put(service.getName(), managedService);
		return managedService;
	}

	public synchronized void release(String serviceId) {
		ManagedServiceImpl manager = this.managedServices.get(serviceId);
		manager.stop();
	}

	public synchronized void manage(EventProcessingAgent agent) {

		ManagedAgent managedAgent = new ManagedAgent(agent, epService);
		if (!managedAgents.containsKey(agent.getId())){
			try {
				managedAgent.start();
				managedAgents.put(agent.getId(), managedAgent);
			
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public synchronized void release(EventProcessingAgent agent) {
		if (this.managedAgents.containsKey(agent.getId())) {
			ManagedAgent manager = this.managedAgents.get(agent.getId());
			try {
				manager.stop();
				this.managedAgents.remove(agent.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	class ManagedServiceImpl implements ManagedService { //implements ManagedServiceMBean {

		private ServiceInstance service;
		private MonitoredService monitoredService;

		public ManagedServiceImpl(ServiceInstance service) {
			this.service = service;
		}

		public void start() {
			this.monitoredService = monitoringService.startMonitoring(service);
			managementInfomationBase.addMonitoredService(monitoredService);
			this.registerMBean();
		}

		public void stop() {
			monitoredService.stop();
			this.unregisterMBean();
		}

		
		
		public void registerMBean() {
			/*
			if (!managedServices.containsKey(service.getName())) {
				try {
					MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
					ObjectName name = this.getObjectName();
					mbeanServer.registerMBean(this, name);
				} catch(Exception e) {
					e.printStackTrace();
				}
			} */
		}
		
		private void unregisterMBean() {
			/*
			if (managedServices.containsKey(service.getName())) {
				try {
					MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
					ObjectName name = this.getObjectName();
					mbeanServer.unregisterMBean(name);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}*/
		}

		private ObjectName getObjectName() throws Exception {
			return new ObjectName(String.format("dsoa:type=monitoredService, name=%s", service.getName()));
		}

		public String getMonitoredServicePid() {
			return monitoredService.getMonitoredServicePid();
		}

		public String getComponentId() {
			return monitoredService.getComponentId();
		}

		public boolean isStarted() {
			return monitoredService.isStarted();
		}

		public String[] getStatusVariableNames() {
			// TODO Auto-generated method stub
			return monitoredService.getStatusVariableNames();
		}

		public StatusVariable getStatusVariable(String id) {
			return monitoredService.getStatusVariable(id);
		}

		public String getDescription(String id) {
			return monitoredService.getDescription(id);
		}

		public Map<String, MonitoredAttribute> getMetricVariableMap() {
			return monitoredService.getMetricVariableMap();
		}
		
	}
}
