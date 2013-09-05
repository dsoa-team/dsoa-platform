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
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.mapper.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredAttribute;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;



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
		MonitoredService monitoredService = null;
		if (nfs != null) {
			monitoredService = new MonitoredService(ctx, service);
			for (AttributeConstraint attributeConstraint : service.getSpecification().getNonFunctionalSpecification().getAttributeConstraints()) {
				this.addAttributeMonitor(monitoredService, attributeConstraint);
			}
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
		return monitoredService;
	}

	public void stopMonitoring(String providedServiceId) {
		List<MonitoredService> monitoredServices = serviceMonitorsMap.get(providedServiceId);
		for (MonitoredService monitoredService : monitoredServices) {
			monitoredService.stop();
		}
	}

	public void addAttributeConstraint(String servicePid, AttributeConstraint attributeConstraint) {
		List<MonitoredService> monitors = serviceMonitorsMap.get(servicePid);
		if (monitors == null || monitors.isEmpty()) {
			log.warning("There is no monitors related to service " + servicePid);
			return;
		}
		MonitoredService monitor = monitors.get(0);
		this.addAttributeMonitor(monitor, attributeConstraint);
	}

	private void addAttributeMonitor(MonitoredService monitoredService, AttributeConstraint attributeConstraint) {
		String attributeId = attributeConstraint.getAttributeId();
		Attribute attribute = attributeCatalog.getAttribute(attributeId);
		if (attribute != null) {
			AttributableId attributableId = new AttributableId(monitoredService.getServiceId(), attributeConstraint.getOperation());
			MonitoredAttribute monitoredAttribute = new MonitoredAttribute(attributableId, attribute);
			monitoredService.addAttributeMonitor(monitoredAttribute);
			AttributeEventMapper mapper = attributeMapperCatalog.getAttributeEventMapper(attributeId);
			if (mapper != null) {
				EventConsumer consumer = new EventConsumerImpl(monitoredAttribute);
				
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
