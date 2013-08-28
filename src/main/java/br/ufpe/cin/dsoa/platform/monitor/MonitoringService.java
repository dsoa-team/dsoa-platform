package br.ufpe.cin.dsoa.platform.monitor;

import java.util.List;

import br.ufpe.cin.dsoa.service.AttributeConstraint;
import br.ufpe.cin.dsoa.service.Service;

public interface MonitoringService {

	List<ServiceMonitor> getMonitoredServices();

	ServiceMonitor getMonitoredService(String id);

	ServiceMonitor startMonitoring(Service service);

	void stopMonitoring(String serviceId);

	void addAttributeConstraint(String servicePid, AttributeConstraint attributeConstraint);

}