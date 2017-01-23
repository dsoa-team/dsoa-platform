package br.ufpe.cin.dsoa.platform.monitor;

import java.util.List;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public interface MonitoringService {

	List<MonitoredService> getMonitoredServices();

	MonitoredService getMonitoredService(String id);

	MonitoredService startMonitoring(ServiceInstance service);

	void stopMonitoring(String serviceId);

	void addMonitoredAttribute(MonitoredService monitoredService, Attribute attribute,
			String operation);

}