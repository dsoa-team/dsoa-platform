package br.ufpe.cin.dsoa.monitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.attribute.AttributableId;
import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.attribute.AttributeId;
import br.ufpe.cin.dsoa.attribute.AttributeMonitor;
import br.ufpe.cin.dsoa.attribute.mappers.AttributeAttributableMapper;
import br.ufpe.cin.dsoa.configurator.parser.attribute.Attribute;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.monitor.MonitoredService;
import br.ufpe.cin.dsoa.monitor.MonitoringService;
import br.ufpe.cin.dsoa.util.Util;

/**
 * This component is responsible for creating and maintaining "monitored services". Each monitored service is,
 * in fact, a service that maintains metric data related to a specific business service.
 *  
 * @author fabions
 *
 */
public class MonitoringServiceImpl implements MonitoringService {

	private EventProcessingService eventProcessingService;
	private AttributeCatalog attributeCatalog;
	
	/**
	 * Maps a service.pid to the service that is created to store the metrics associated to the service.
	 * That is: Map<service.pid, MonitoredService>
	 * */
	private Map<String, MonitoredService> monitoredServiceMap = new HashMap<String, MonitoredService>();
	
	
	public void startMonitoring(ServiceReference reference, List<AttributeAttributableMapper> attributeAttributableMappers) {
		MonitoredService monitoredService = new MonitoredService(reference);
		for (AttributeAttributableMapper attributeAttributableMapper : attributeAttributableMappers) {
			this.addMetricMonitor(monitoredService, attributeAttributableMapper);
		}
		synchronized (monitoredServiceMap) {
			monitoredServiceMap.put(monitoredService.getPid(), monitoredService);
		}
		monitoredService.start();
	}
	
	public void stopMonitoring(ServiceReference reference) {
		MonitoredService monitoredService = monitoredServiceMap.get(Util.getPid(reference));
		monitoredService.stop();
	}
	
	public void addMetric(String servicePid, AttributeAttributableMapper attributeAttributableMapper) {
		this.addMetricMonitor(monitoredServiceMap.get(servicePid), attributeAttributableMapper);
	}
	
	
	public List<MonitoredService> getMonitoredServices(){
		List<MonitoredService> services = new ArrayList<MonitoredService>(this.monitoredServiceMap.values());
		return services;
	}
	
	public MonitoredService getMonitoredService(String id){
		MonitoredService services = this.monitoredServiceMap.get(id);
		return services;
	}
	
	private void addMetricMonitor(MonitoredService monitoredService, AttributeAttributableMapper attributeAttributableMapper) {
		AttributeId attributeId = attributeAttributableMapper.getAtttributeId();
		Attribute attribute = attributeCatalog.getAttribute(attributeId);
		if (attribute != null) {
			AttributableId attributableId = attributeAttributableMapper.getAttributableId();
			AttributeMonitor monitor = new AttributeMonitor(attributableId, attribute);
			monitoredService.addMetricMonitor(monitor);
			String stmtName = monitor.getStatusVariableId();
			String stmt = attribute.getQuery();
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(attributableId.getId());
			eventProcessingService.subscribe(attributeAttributableMapper.toString(), parameters, monitor);
		}
	}
}
