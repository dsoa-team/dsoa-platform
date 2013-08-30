package br.ufpe.cin.dsoa.platform.monitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.attribute.AttributableId;
import br.ufpe.cin.dsoa.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.attribute.meta.AttributeType;
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

	private Logger log;
	
	private BundleContext ctx;
	
	private EventProcessingService eventProcessingService;
	
	private AttributeCatalog attributeCatalog;
	
	private AttributeEventMapperCatalog attributeMapperCatalog;
	
	/**
	 * Maps a service.pid to the service that is created to store the metrics
	 * associated to the service. That is: Map<service.pid, ServiceMonitor>
	 */
	private Map<String, List<ServiceMonitor>> serviceMonitorsMap = new HashMap<String, List<ServiceMonitor>>();

	public MonitoringServiceImpl(BundleContext ctx) {
		this.ctx = ctx;
		this.log = Logger.getLogger(MonitoringService.class.getName());
	}
	
	public List<ServiceMonitor> getMonitoredServices() {
		 List<ServiceMonitor> list = new ArrayList<ServiceMonitor>();
		 for (List<ServiceMonitor> providedList : serviceMonitorsMap.values()) {
			 list.addAll(providedList);
		 }
		 return list;
	}
	
	public ServiceMonitor getMonitoredService(String providedId) {
		List<ServiceMonitor> compInstanceList = this.serviceMonitorsMap.get(providedId);
		if (providedId != null && !compInstanceList.isEmpty()) {
			return compInstanceList.get(0);
		} else {
			return null;
		}
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
				String providedServiceId = service.getServiceId();
				List<ServiceMonitor> monitors = serviceMonitorsMap.get(providedServiceId);
				if (monitors == null) {
					monitors = new ArrayList<ServiceMonitor>();
					serviceMonitorsMap.put(providedServiceId, monitors);
				}
				monitors.add(serviceMonitor);
			}
			serviceMonitor.start();
		}
		return serviceMonitor;
	}

	public void stopMonitoring(String providedServiceId) {
		List<ServiceMonitor> serviceMonitors = serviceMonitorsMap.get(providedServiceId);
		for (ServiceMonitor serviceMonitor : serviceMonitors) {
			serviceMonitor.stop();
		}
	}

	public void addAttributeConstraint(String servicePid, AttributeConstraint attributeConstraint) {
		List<ServiceMonitor> monitors = serviceMonitorsMap.get(servicePid);
		if (monitors == null || monitors.isEmpty()) {
			log.warning("There is no monitors related to service " + servicePid);
			return;
		}
		ServiceMonitor monitor = monitors.get(0);
		this.addAttributeMonitor(monitor, attributeConstraint);
	}

	private void addAttributeMonitor(ServiceMonitor serviceMonitor, AttributeConstraint attributeConstraint) {
		String attributeId = attributeConstraint.getAttributeId();
		AttributeType attribute = attributeCatalog.getAttribute(attributeId);
		if (attribute != null) {
			AttributableId attributableId = new AttributableId(serviceMonitor.getServiceId(), attributeConstraint.getOperation());
			MonitoredAttribute monitor = new MonitoredAttribute(attributableId, attribute);
			serviceMonitor.addAttributeMonitor(monitor);
			AttributeEventMapper mapper = attributeMapperCatalog.getAttributeEventMapper(attributeId);
			if (mapper != null) {
				String eventType = mapper.getEventType();
				EventConsumer consumer;
				
			}
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
