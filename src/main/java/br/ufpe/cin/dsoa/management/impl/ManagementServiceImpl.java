package br.ufpe.cin.dsoa.management.impl;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.dsoa.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.agent.AgentCatalog;
import br.ufpe.cin.dsoa.attribute.AttributableId;
import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.attribute.AttributeParser;
import br.ufpe.cin.dsoa.management.ManagementService;
import br.ufpe.cin.dsoa.mapper.AttributeAttributableMapper;
import br.ufpe.cin.dsoa.monitor.MonitoredService;
import br.ufpe.cin.dsoa.monitor.MonitoredServiceMetadata;
import br.ufpe.cin.dsoa.monitor.MonitoringService;
import br.ufpe.cin.dsoa.util.Util;

/**
 * The Management Service is responsible for providing an access point to perform
 * management related activities. It is exposed as remote service in order to allow remote
 * administration.
 * 
 * It also listen for the registration of services that are checked as service.managed (through its
 * properties). Those services are supposed to be managed. In this context, the ManagementServiceImpl
 * creates a proxy that intercepts requests directed to the service and create an InvocationEvent that is
 * sent to the EventProcessingService to generate corresponding metrics. In this context, the proxy acts as 
 * an event source.
 * 
 * @author fabions
 *
 */
public class ManagementServiceImpl implements ManagementService {

	private AttributeCatalog attributeCatalog;
	private AgentCatalog agentCatalog;
	private MonitoringService monitoringService;

	public List<MonitoredServiceMetadata> getManagedServicesMetadata() {
		List<MonitoredServiceMetadata> metadata = new ArrayList<MonitoredServiceMetadata>();
		for (MonitoredService monitoredService : this.monitoringService.getMonitoredServices()) {
			metadata.add(monitoredService.getMetadata());
		}
		return metadata;
	}
	
	public MonitoredServiceMetadata getManagedServiceMetadata(String id){
		MonitoredService service = this.monitoringService.getMonitoredService(id);
		MonitoredServiceMetadata metadata = null;
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
	
	public void addAttribute(String category, String name, String servicePid, String operationName) {
		AttributableId attributableId = new AttributableId(servicePid, operationName);
		AttributeAttributableMapper attributeAttributableMapper = new AttributeAttributableMapper(AttributeParser.format(category, name), attributableId);
		this.monitoringService.addMetric(servicePid, attributeAttributableMapper);
	}
	
	public void addAttributeMonitor(String servicePid, String attName, String attCategory, String operationName) {
		Attribute attribute = this.attributeCatalog.getAttribute(AttributeParser.format(attCategory, attName));
		AttributeAttributableMapper attributeAttributableMapper = new AttributeAttributableMapper(attribute.getId(), Util.getAttributableId(servicePid, operationName));
		this.monitoringService.addMetric(servicePid, attributeAttributableMapper);
	}

}
