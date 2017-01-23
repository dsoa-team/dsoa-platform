package br.ufpe.cin.dsoa.platform.management.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.platform.management.ManagementInfomationBase;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;

public class DsoaManagementInformationBase implements ManagementInfomationBase {

	// map<componentId, <specification, monitoredService>>
	private Map<String, Map<String, MonitoredService>> serviceMonitorsMap = 
			new HashMap<String, Map<String, MonitoredService>>();

	public MonitoredService getMonitoredService(String componentId, String specification) {
		MonitoredService monitoredService = null;

		Map<String, MonitoredService> monitoredServices = this.serviceMonitorsMap.get(componentId);
		if (monitoredServices != null) {
			monitoredService = monitoredServices.get(specification);
		}
		return monitoredService;
	}

	public synchronized void addMonitoredService(MonitoredService monitoredService) {

		String componentId = monitoredService.getComponentId();
		String specification = monitoredService.getServiceInstance().getPort().getServiceSpecification().getFunctionalInterface().getInterfaceName();

		Map<String, MonitoredService> monitoredServices = this.getMonitoredServicesMap(componentId);
		monitoredServices.put(specification, monitoredService);

		this.serviceMonitorsMap.put(componentId, monitoredServices);

	}

	public List<MonitoredService> getMonitoredService(String componentId) {

		Map<String, MonitoredService> monitoredServicesMap = this.serviceMonitorsMap
				.get(componentId);
		
		List<MonitoredService> monitoredServices = this.toList(monitoredServicesMap);

		return monitoredServices;
	}

	private Map<String, MonitoredService> getMonitoredServicesMap(String componentId) {

		Map<String, MonitoredService> monitoredServices = this.serviceMonitorsMap.get(componentId);

		if (monitoredServices == null) {
			monitoredServices = new HashMap<String, MonitoredService>();
		}

		return monitoredServices;
	}

	private <T> List<T> toList(Map<String, T> map) {
		
		List<T> list = new ArrayList<T>();
		
		if(map != null) {
			Collection<T> values = map.values();
			if (values != null && !values.isEmpty()) {
				list.addAll(values);
			}
		}

		return list;
	}

}
