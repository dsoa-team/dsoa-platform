package br.ufpe.cin.dsoa.management.impl;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.dsoa.attribute.AttributableId;
import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.attribute.AttributeId;
import br.ufpe.cin.dsoa.attribute.mappers.AttributeAttributableMapper;
import br.ufpe.cin.dsoa.configurator.parser.attribute.Attribute;
import br.ufpe.cin.dsoa.management.ManagementService;
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

	public List<String> getMetricList() {
		List<String> metricList = new ArrayList<String>();
		
		for(Attribute m : this.attributeCatalog.getAttributes()){
			metricList.add(m.toString());
		}
		return metricList;
	}
	
	public void addMetric(String category, String name, String servicePid, String operationName) {
		AttributeId attributeId = new AttributeId(category, name);
		AttributableId attributableId = new AttributableId(servicePid, operationName);
		AttributeAttributableMapper attributeAttributableMapper = new AttributeAttributableMapper(attributeId, attributableId);
		this.monitoringService.addMetric(servicePid, attributeAttributableMapper);
	}
	
	public void addMetricMonitor(String servicePid, String metricName, String metricCategory, String operationName) {
		Attribute attribute = this.attributeCatalog.getAttribute(new AttributeId(metricCategory, metricName));
		AttributeAttributableMapper attributeAttributableMapper = new AttributeAttributableMapper(attribute.getId(), Util.getAttributableId(servicePid, operationName));
		this.monitoringService.addMetric(servicePid, attributeAttributableMapper);
	}
}
