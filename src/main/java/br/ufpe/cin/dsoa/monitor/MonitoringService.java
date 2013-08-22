package br.ufpe.cin.dsoa.monitor;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.mapper.AttributeAttributableMapper;


public interface MonitoringService {
	void startMonitoring(ServiceReference reference, List<AttributeAttributableMapper> attributeAttributableMappers);
	void stopMonitoring(ServiceReference reference);
	List<MonitoredService> getMonitoredServices();
	MonitoredService getMonitoredService(String id);
	void addMetric(String servicePid, AttributeAttributableMapper attributeAttributableMapper);
	
}
