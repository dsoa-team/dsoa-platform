package br.ufpe.cin.dsoa.platform.resource.impl;

import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import br.ufpe.cin.dsoa.api.event.EventProcessingService;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.platform.management.ManagementInfomationBase;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.platform.resource.ResourceManager;
import br.ufpe.cin.dsoa.platform.resource.mbean.ManagedAgent;

public class ResourceManagerImpl implements ResourceManager {

	private MonitoringService monitoringService;
	private ManagementInfomationBase managementInfomationBase;
	private EventProcessingService epService;
	//private ProxyFactory proxyFactory;
	
	private Map<String, ManagedService> managedServices = new HashMap<String, ManagedService>();
	private Map<String, ManagedAgent> managedAgents = new HashMap<String, ManagedAgent>();

	public synchronized void manage(Service service) {
		ManagedService serviceManager = new ManagedService(service);
		serviceManager.start();
		this.managedServices.put(service.getProviderId(), serviceManager);
	}

	public synchronized void release(String serviceId) {
		ManagedService manager = this.managedServices.get(serviceId);
		manager.stop();
	}

	public synchronized void manage(EventProcessingAgent agent) {

		ManagedAgent managedAgent = new ManagedAgent(agent, epService);
		try {
			managedAgent.start();
			managedAgents.put(agent.getId(), managedAgent);
			
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (InstanceAlreadyExistsException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			System.err.println(e.getMessage());
		}
	}

	public synchronized void release(EventProcessingAgent agent) {
		ManagedAgent manager = this.managedAgents.get(agent.getId());
		
		try {
			manager.stop();
			this.managedAgents.remove(agent.getId());
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	class ManagedService {

		private Service service;
		private MonitoredService monitoredService;
/*		private ServiceEvaluator serviceEvaluator;
		private ServiceMBean serviceMBean;*/

		public ManagedService(Service service) {
			this.service = service;
//			this.serviceEvaluator = new ServiceEvaluator(service);
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
		}

		private void unregisterMBean() {

		}

	}
}
