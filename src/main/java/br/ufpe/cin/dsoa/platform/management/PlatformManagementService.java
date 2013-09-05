package br.ufpe.cin.dsoa.platform.management;

import java.util.List;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.platform.monitor.ServiceMetadata;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;

/**
 * This intends to be the DSOA's management facade. 
 * 
 * @author dsoa-team
 *
 */
public interface PlatformManagementService {

	List<String> getAttributeList();
	
	void addAttributeMonitor(String servicePid, String attName, String attCategory, String operationName);
	
	public ServiceMetadata getManagedServiceMetadata(String id);
	
	public List<ServiceMetadata> getManagedServicesMetadata();
	
	public List<String> getAgentList();
	
	List<MonitoredService> getMonitoredServices();
	
	MonitoredService getMonitoredService(String id);
	
	void addAttributeConstraint(String serviceId, AttributeConstraint attributeConstraint);
}