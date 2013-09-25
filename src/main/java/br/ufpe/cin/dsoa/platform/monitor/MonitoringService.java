package br.ufpe.cin.dsoa.platform.monitor;

import java.util.List;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.service.Service;

public interface MonitoringService {

	List<MonitoredService> getMonitoredServices();

	MonitoredService getMonitoredService(String id);

	MonitoredService startMonitoring(Service service);

	void stopMonitoring(String serviceId);

	void addMonitoredAttribute(MonitoredService monitoredService, Attribute attribute,
			String operation);

}