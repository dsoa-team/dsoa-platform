package br.ufpe.cin.dsoa.monitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.attribute.AttributableId;
import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.attribute.AttributeMonitor;
import br.ufpe.cin.dsoa.attribute.AttributeParser;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.mapper.AttributeAttributableMapper;
import br.ufpe.cin.dsoa.monitor.MonitoredService;
import br.ufpe.cin.dsoa.monitor.MonitoringService;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.Util;

/**
 * 
 * This component is responsible for listening for the registration of services that are intended to be monitored (As
 * indicated by the monitored.service property). For each service that is registered, it creates and maintains a "monitored service",
 * which is a service that maintains metrics related to a corresponding business service. 
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
			this.addAttributeMonitor(monitoredService, attributeAttributableMapper);
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
		this.addAttributeMonitor(monitoredServiceMap.get(servicePid), attributeAttributableMapper);
	}
	
	
	public List<MonitoredService> getMonitoredServices(){
		List<MonitoredService> services = new ArrayList<MonitoredService>(this.monitoredServiceMap.values());
		return services;
	}
	
	public MonitoredService getMonitoredService(String id){
		MonitoredService services = this.monitoredServiceMap.get(id);
		return services;
	}
	
	private void addAttributeMonitor(MonitoredService monitoredService, AttributeAttributableMapper attributeAttributableMapper) {
		String attributeId = attributeAttributableMapper.getAtttributeId();
		Attribute attribute = attributeCatalog.getAttribute(attributeId);
		if (attribute != null) {
			AttributableId attributableId = attributeAttributableMapper.getAttributableId();
			AttributeMonitor monitor = new AttributeMonitor(attributableId, attribute);
			monitoredService.addAttributeMonitor(monitor);
			String stmtName = monitor.getStatusVariableId();
			//String stmt = attribute.getQuery();
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(attributableId.getId());
			eventProcessingService.subscribe(attributeAttributableMapper.toString(), parameters, monitor);
		}
	}
	
	/**
	 * This method is called when a is registered and it has a property with name monitored.service set.
	 * It is responsible for parsing the metrics that should be monitored (indicated through the service's properties)
	 * and starting monitoring them. To do that, it creates proxy service that intercepts requests and creates an 
	 * InvocationEvent that is sent to the EventProcessingService. There, there are Property Computing Agents that do the
	 * real metric computation. For each service that is monitored, the Monitoring Service also creates a MonitoredService
	 * that stores corresponding metrics and implements Monitorable interface (see OSGi spec).
	 */
	public void onArrival(ServiceReference reference) {
		Boolean isProxy = ((Boolean)reference.getProperty(Constants.SERVICE_PROXY)==null ? false : Boolean.valueOf(reference.getProperty(Constants.SERVICE_PROXY).toString()));
		if (!isProxy) {
			AttributeAttributableMapper attributeAttributableMapper = null;
			String servicePid = Util.getPid(reference);
			String keys[] = reference.getPropertyKeys();
			List<AttributeAttributableMapper> attributeAttributableMappers = new ArrayList<AttributeAttributableMapper>();
			for (String key : keys) {
				attributeAttributableMapper = AttributeParser.parse(servicePid, key);
				if (attributeAttributableMapper != null) {
					attributeAttributableMappers.add(attributeAttributableMapper);
				}
			}
			if (!attributeAttributableMappers.isEmpty()) {
				this.startMonitoring(reference, attributeAttributableMappers);
			}
		}
	}
	
	public void onDeparture(ServiceReference reference) {
		this.stopMonitoring(reference);
	}
}
