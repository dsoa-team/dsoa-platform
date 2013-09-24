package br.ufpe.cin.dsoa.platform.monitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.api.attribute.AttributableId;
import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.FilterExpression;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Expression;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredAttribute;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.util.Constants;



/**
 * This component is responsible for monitoring services that are registered in the platform. It reads the service
 * non-functional specification, identify declared QoS attributes, and starts a monitor (an agent processing instance)
 * that computes corresponding attribute values. It is important to mention that its monitoring action is passive, in the
 * sense that the service DOESN'T generate artificial requests. It computes metrics based on InvocationEvents that
 * are generated when the service is requested. In fact, it register agents in the EventProcessingService, which are 
 * able to compute the corresponding metrics.
		
 * @author fabions
 *
 */
public class MonitoringServiceImpl implements MonitoringService {

	private Logger log;
	
	private BundleContext ctx;
	
	private EventProcessingService eventProcessingService;
	
	private AttributeCatalog attributeCatalog;
	
	private AttributeEventMapperCatalog attributeMapperCatalog;
	
	/**
	 * Maps a service.pid to the service that is created to store the metrics
	 * associated to the service. That is: Map<service.pid, MonitoredService>
	 */
	private Map<String, List<MonitoredService>> serviceMonitorsMap = new HashMap<String, List<MonitoredService>>();

	public MonitoringServiceImpl(BundleContext ctx) {
		this.ctx = ctx;
		this.log = Logger.getLogger(MonitoringService.class.getName());
	}
	
	public List<MonitoredService> getMonitoredServices() {
		 List<MonitoredService> list = new ArrayList<MonitoredService>();
		 for (List<MonitoredService> providedList : serviceMonitorsMap.values()) {
			 list.addAll(providedList);
		 }
		 return list;
	}
	
	public MonitoredService getMonitoredService(String providedId) {
		List<MonitoredService> compInstanceList = this.serviceMonitorsMap.get(providedId);
		if (providedId != null && !compInstanceList.isEmpty()) {
			return compInstanceList.get(0);
		} else {
			return null;
		}
	}
	
	public MonitoredService startMonitoring(Service service) {
		NonFunctionalSpecification nfs = service.getSpecification().getNonFunctionalSpecification();
		MonitoredService monitoredService = new MonitoredService(ctx, service);
		if (nfs != null) {
			for (AttributeConstraint attributeConstraint : service.getSpecification().getNonFunctionalSpecification().getAttributeConstraints()) {
				
				String servicePid = service.getServiceId();
				String operation = attributeConstraint.getOperation();
				String attributeId = attributeConstraint.getAttributeId();
				this.addMonitoredAttribute(servicePid, operation, attributeId);
				
			}
		}
		registerMonitoredService(service, monitoredService);
		
		return monitoredService;
	}

	private void registerMonitoredService(Service service, MonitoredService monitoredService) {
		synchronized (serviceMonitorsMap) {
			String providedServiceId = service.getServiceId();
			List<MonitoredService> monitors = serviceMonitorsMap.get(providedServiceId);
			if (monitors == null) {
				monitors = new ArrayList<MonitoredService>();
				serviceMonitorsMap.put(providedServiceId, monitors);
			}
			monitors.add(monitoredService);
		}
		
		monitoredService.start();
	}

	public void stopMonitoring(String providedServiceId) {
		List<MonitoredService> monitoredServices = serviceMonitorsMap.get(providedServiceId);
		for (MonitoredService monitoredService : monitoredServices) {
			monitoredService.stop();
		}
	}

	public void addMonitoredAttribute(String servicePid, String operation, String attributeId) {
		
		Attribute attribute = attributeCatalog.getAttribute(attributeId);
		if (attribute != null) {
			
			AttributableId attributableId = new AttributableId(servicePid, operation);
			
			//add monitored attribute
			List<MonitoredService> monitoredServices = this.serviceMonitorsMap.get(servicePid);
			if(monitoredServices != null){
				for(MonitoredService monitoredService : monitoredServices) {
					MonitoredAttribute monitoredAttribute = new MonitoredAttribute(ctx, monitoredService.getMonitoredServicePid(), attributableId, attribute);
					monitoredService.addMonitoredAttribute(monitoredAttribute);

					AttributeEventMapper mapper = attributeMapperCatalog.getAttributeEventMapper(attributeId);
					if (mapper != null) {
						EventConsumer consumer = new EventConsumerImpl(mapper, monitoredAttribute);
						EventType eventType = mapper.getEventType();
						PropertyType sourceType = eventType.getMetadataPropertyType(Constants.EVENT_SOURCE);
						
						FilterExpression filterExp = new FilterExpression(new Property(attributableId.getId(), sourceType), Expression.EQ);
						List<FilterExpression> filterList = new ArrayList<FilterExpression>();
						filterList.add(filterExp);
						EventFilter filter = new EventFilter(filterList);
						
						String id = sourceType + Constants.TOKEN + attributeId;
						Subscription subscription = new Subscription(id, eventType, filter);
						eventProcessingService.subscribe(consumer, subscription);
					}
				}
			}
			
		}
		
	}
	
}
