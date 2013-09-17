package br.ufpe.cin.dsoa.platform.resource.impl;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.platform.management.jmx.ServiceMBean;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.platform.resource.ResourceManager;

public class ResourceManagerImpl implements ResourceManager {
	
	private MonitoringService monitoringService;
	private Map<String, ServiceManager> managedServices = new HashMap<String, ServiceManager>();
	
	public synchronized void manage(Service service) {
		ServiceManager serviceManager = new ServiceManager(service);
		serviceManager.start();
		this.managedServices.put(service.getServiceId(), serviceManager);
	}
	
	public synchronized void release(String serviceId) {
		ServiceManager manager = this.managedServices.get(serviceId);
		manager.stop();
	}
	
	class ServiceManager {

		private Service service;
		private ServiceMBean serviceMBean;
		private MonitoredService monitoredService;
		
		public ServiceManager(Service service) {
			this.service = service;
		}

		public void start() {
			monitoredService = monitoringService.startMonitoring(service);
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
