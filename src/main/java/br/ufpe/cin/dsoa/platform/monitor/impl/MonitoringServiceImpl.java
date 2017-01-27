package br.ufpe.cin.dsoa.platform.monitor.impl;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.api.attribute.AttributableId;
import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.EventProcessingService;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.FilterExpression;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.RelationalOperator;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredAttribute;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringRegistration;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.util.Constants;

/**
 * This component is responsible for monitoring services that are registered in
 * the platform. It reads the service non-functional specification, identify
 * declared QoS attributes, and starts a monitor (an agent processing instance)
 * that computes corresponding attribute values. It is important to mention that
 * its monitoring action is passive, in the sense that the service DOESN'T
 * generate artificial requests. It computes metrics based on InvocationEvents
 * that are generated when the service is requested. In fact, it register agents
 * in the EventProcessingService, which are able to compute the corresponding
 * metrics.
 * 
 * @author fabions
 * 
 */
public class MonitoringServiceImpl implements MonitoringService {

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

	public MonitoredService startMonitoring(ServiceInstance service) {
		NonFunctionalSpecification nfs = service.getPort().getServiceSpecification().getNonFunctionalSpecification();
		MonitoredService monitoredService = new MonitoredService(ctx, service);
		if (nfs != null) {
			for (Constraint attributeConstraint : service.getPort().getServiceSpecification().getNonFunctionalSpecification().getConstraints()) {

				String operation = attributeConstraint.getOperation();
				String attributeId = attributeConstraint.getAttributeId();
				Attribute attribute = this.attributeCatalog.getAttribute(attributeId);
				Collection<Attribute> atts = this.attributeCatalog.getAttributes();
				for(Attribute att : atts) {
					System.out.println("AttId: " + att.getId());
					System.out.println("AttName: " + att.getName());
					System.out.println("AttDesc: " + att.getDescription());
				}
				
				if (attribute == null) {
					throw new InvalidParameterException("Attribute doesn't exists: " + operation + ":" + attributeId);
				}

				this.addMonitoredAttribute(monitoredService, attribute, operation);

			}
		}
		registerMonitoredService(service, monitoredService);

		return monitoredService;
	}

	private void registerMonitoredService(ServiceInstance service, MonitoredService monitoredService) {
		synchronized (serviceMonitorsMap) {
			String serviceName = service.getName();
			List<MonitoredService> monitors = serviceMonitorsMap.get(serviceName);
			if (monitors == null) {
				monitors = new ArrayList<MonitoredService>();
				serviceMonitorsMap.put(serviceName, monitors);
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

	public void addMonitoredAttribute(MonitoredService monitoredService, Attribute attribute,
			String operation) {

		String serviceId = monitoredService.getComponentId();
		String attributeId = attribute.getId();
		AttributableId attributableId = new AttributableId(serviceId, operation);

		final MonitoredAttribute monitoredAttribute = new MonitoredAttribute(ctx,
				monitoredService.getMonitoredServicePid(), attributableId, attribute);

		// add monitored attribute
		monitoredService.addMonitoredAttribute(monitoredAttribute);

		final AttributeEventMapper mapper = attributeMapperCatalog
				.getAttributeEventMapper(attributeId);
		if (mapper != null) {
			EventConsumer consumer = new EventConsumer() {
				private String id = monitoredAttribute.getStatusVariableId();

				@Override
				public String getComponentInstanceName() {
					return id;
				}

				@Override
				public void handleEvent(Event event) {
					AttributeValue attValue = mapper.convertToAttribute(event);
					monitoredAttribute.update(attValue);
				}
			};

			Subscription subscription = this.createSubscription(monitoredAttribute, attributableId,
					attributeId, mapper);

			monitoredAttribute.setMonitoringRegistration(new MonitoringRegistration(consumer,
					subscription));

			eventProcessingService.subscribe(consumer, subscription, true);
		}

	}

	private Subscription createSubscription(MonitoredAttribute monitoredAttribute,
			AttributableId attributableId, String attributeId, AttributeEventMapper mapper) {

		EventType eventType = mapper.getEventType();
		PropertyType sourceType = eventType.getMetadataPropertyType(Constants.EVENT_SOURCE);

		FilterExpression filterExp = new FilterExpression(new Property(attributableId.getId(),
				sourceType), RelationalOperator.EQ);
		List<FilterExpression> filterList = new ArrayList<FilterExpression>();
		filterList.add(filterExp);
		EventFilter filter = new EventFilter(filterList);

		Subscription subscription = new Subscription(eventType, filter);

		return subscription;
	}
}
