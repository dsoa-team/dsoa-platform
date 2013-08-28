package br.ufpe.cin.dsoa.platform.monitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.attribute.AttributableId;
import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.event.EventConsumer;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.mapper.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredAttribute;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.platform.monitor.ServiceMonitor;
import br.ufpe.cin.dsoa.service.AttributeConstraint;
import br.ufpe.cin.dsoa.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.service.Service;

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

	private BundleContext ctx;
	
	private EventProcessingService eventProcessingService;
	
	private AttributeCatalog attributeCatalog;
	
	private AttributeEventMapperCatalog attributeMapperCatalog;
	
	/**
	 * Maps a service.pid to the service that is created to store the metrics
	 * associated to the service. That is: Map<service.pid, ServiceMonitor>
	 */
	private Map<String, ServiceMonitor> serviceMonitorsMap = new HashMap<String, ServiceMonitor>();

	public MonitoringServiceImpl(BundleContext ctx) {
		this.ctx = ctx;
	}
	
	public List<ServiceMonitor> getMonitoredServices() {
		return new ArrayList<ServiceMonitor>(serviceMonitorsMap.values());
	}
	
	public ServiceMonitor getMonitoredService(String id) {
		return this.serviceMonitorsMap.get(id);
	}
	
	public ServiceMonitor startMonitoring(Service service) {
		NonFunctionalSpecification nfs = service.getSpecification().getNonFunctionalSpecification();
		ServiceMonitor serviceMonitor = null;
		if (nfs != null) {
			serviceMonitor = new ServiceMonitor(ctx, service);
			for (AttributeConstraint attributeConstraint : service.getSpecification().getNonFunctionalSpecification().getAttributeConstraints()) {
				this.addAttributeMonitor(serviceMonitor, attributeConstraint);
			}
			synchronized (serviceMonitorsMap) {
				serviceMonitorsMap.put(service.getServiceId(), serviceMonitor);
			}
			serviceMonitor.start();
		}
		return serviceMonitor;
	}

	public void stopMonitoring(String serviceId) {
		ServiceMonitor serviceMonitor = serviceMonitorsMap.get(serviceId);
		serviceMonitor.stop();
	}

	public void addAttributeConstraint(String servicePid, AttributeConstraint attributeConstraint) {
		this.addAttributeMonitor(serviceMonitorsMap.get(servicePid), attributeConstraint);
	}

	private void addAttributeMonitor(ServiceMonitor serviceMonitor, AttributeConstraint attributeConstraint) {
		String attributeId = attributeConstraint.getAttributeId();
		Attribute attribute = attributeCatalog.getAttribute(attributeId);
		if (attribute != null) {
			AttributableId attributableId = new AttributableId(serviceMonitor.getServiceId(), attributeConstraint.getOperation());
			MonitoredAttribute monitor = new MonitoredAttribute(attributableId, attribute);
			serviceMonitor.addAttributeMonitor(monitor);
			AttributeEventMapper mapper = attributeMapperCatalog.getAttributeEventMapper(attributeId);
			String eventType = mapper.getEventType();
			EventConsumer consumer;
			//eventProcessingService.registerConsumer(consumer);
		// String stmtName = monitor.getStatusVariableId();
		// AttributeEventMapper attEventMapper =
		// String stmt = attribute.getQuery();
		// List<Object> parameters = new ArrayList<Object>();
		// parameters.add(attributableId.getId());
		// eventProcessingService.subscribe(attributeAttributableMapper.toString(),
		// parameters, monitor);
		}
	}
	
}
