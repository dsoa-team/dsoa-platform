package br.ufpe.cin.dsoa.platform.management.impl;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.event.AgentCatalog;
import br.ufpe.cin.dsoa.platform.management.PlatformManagementService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.platform.monitor.ServiceMetadata;
import br.ufpe.cin.dsoa.util.Constants;

/**
 * The Management Service is responsible for providing an access point to perform
 * management related activities. It is exposed as remote service in order to allow remote
 * administration.
 * 
 * @author fabions
 *
 */
public class PlatformManagementServiceImpl implements PlatformManagementService {

	private AttributeCatalog attributeCatalog;
	
	private AgentCatalog agentCatalog;
	
	private MonitoringService monitoringService;
	
	public List<ServiceMetadata> getManagedServicesMetadata() {
		List<ServiceMetadata> metadata = new ArrayList<ServiceMetadata>();
		for (MonitoredService monitoredService : this.monitoringService.getMonitoredServices()) {
			metadata.add(monitoredService.getMetadata());
		}
		return metadata;
	}
	
	public ServiceMetadata getManagedServiceMetadata(String id){
		MonitoredService service = this.monitoringService.getMonitoredService(id);
		ServiceMetadata metadata = null;
		if(null != service) {
			metadata = service.getMetadata();
		}
		
		return metadata;
	}

	public List<String> getAttributeList() {
		List<String> attributeList = new ArrayList<String>();
		
		for(Attribute m : this.attributeCatalog.getAttributes()){
			attributeList.add(m.toString());
		}
		return attributeList;
	}
	
	public List<String> getAgentList() {
		List<String> agentList = new ArrayList<String>();
		
		for(EventProcessingAgent eventProcessingAgent : this.agentCatalog.getAgents()){
			agentList.add(eventProcessingAgent.toString());
		}
		
		return agentList;
	}
	
	public List<MonitoredService> getMonitoredServices() {
		return monitoringService.getMonitoredServices();
	}

	public MonitoredService getMonitoredService(String id) {
		return monitoringService.getMonitoredService(id);
	}

	public void addAttributeMonitor(String servicePid, String attName, String attCategory, String operationName) {
		String attributeId = String.format("%s%s%s", attCategory, Constants.TOKEN, attName);
		this.monitoringService.addMonitoredAttribute(servicePid, operationName, attributeId);
	}
	
}
