package br.ufpe.cin.dsoa.platform.resource.impl;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.platform.management.ManagementInfomationBase;
import br.ufpe.cin.dsoa.platform.management.jmx.ServiceMBean;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.platform.resource.ResourceManager;

public class ResourceManagerImpl implements ResourceManager {
	
	private MonitoringService monitoringService;
	private ManagementInfomationBase managementInfomationBase;
	private Map<String, ManagedService> managedServices = new HashMap<String, ManagedService>();
	
	public synchronized void manage(Service service) {
		ManagedService serviceManager = new ManagedService(service);
		serviceManager.start();
		this.managedServices.put(service.getCompomentId(), serviceManager);
	}
	
	public synchronized void release(String serviceId) {
		ManagedService manager = this.managedServices.get(serviceId);
		manager.stop();
	}
	
	class ManagedService {

		private Service service;
		private ServiceMBean serviceMBean;
		private MonitoredService monitoredService;
		
		public ManagedService(Service service) {
			this.service = service;
		}

		public void start() {
			monitoredService = monitoringService.startMonitoring(service);
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
