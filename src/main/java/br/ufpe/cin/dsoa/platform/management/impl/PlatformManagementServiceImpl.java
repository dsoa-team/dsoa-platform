package br.ufpe.cin.dsoa.platform.management.impl;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.dsoa.attribute.meta.AttributeType;
import br.ufpe.cin.dsoa.event.agent.meta.EventProcessingAgent;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.event.AgentCatalog;
import br.ufpe.cin.dsoa.platform.management.PlatformManagementService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.platform.monitor.ServiceMetadata;
import br.ufpe.cin.dsoa.platform.monitor.ServiceMonitor;
import br.ufpe.cin.dsoa.service.AttributeConstraint;
import br.ufpe.cin.dsoa.util.AttributeParser;

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
		for (ServiceMonitor serviceMonitor : this.monitoringService.getMonitoredServices()) {
			//TODO: CORRIGIR
			//metadata.add(serviceMonitor.getMetadata());
		}
		return metadata;
	}
	
	public ServiceMetadata getManagedServiceMetadata(String id){
		ServiceMonitor service = this.monitoringService.getMonitoredService(id);
		ServiceMetadata metadata = null;
		if(null != service) {
			//TODO: CORRIGIR
			//metadata = service.getMetadata();
		}
		
		return metadata;
	}

	public List<String> getAttributeList() {
		List<String> attributeList = new ArrayList<String>();
		
		for(AttributeType m : this.attributeCatalog.getAttributes()){
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
	
	public List<ServiceMonitor> getMonitoredServices() {
		return monitoringService.getMonitoredServices();
	}

	public ServiceMonitor getMonitoredService(String id) {
		return monitoringService.getMonitoredService(id);
	}

	public void addAttributeConstraint(String serviceId, AttributeConstraint attributeConstraint) {
		this.monitoringService.addAttributeConstraint(serviceId, attributeConstraint);
	}

	public void addAttributeMonitor(String servicePid, String attName, String attCategory, String operationName) {
		AttributeType attribute = this.attributeCatalog.getAttribute(AttributeParser.format(attCategory, attName));
		AttributeConstraint attributeConstraint = null; 
		// TODO: terminar
		//new AttributeConstraint(attribute.getId(), Util.getAttributableId(servicePid, operationName));
		this.monitoringService.addAttributeConstraint(servicePid, attributeConstraint);
	}
	
}
