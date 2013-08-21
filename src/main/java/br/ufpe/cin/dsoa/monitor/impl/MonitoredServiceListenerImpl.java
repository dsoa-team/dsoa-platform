package br.ufpe.cin.dsoa.monitor.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.attribute.AttributeParser;
import br.ufpe.cin.dsoa.attribute.mappers.AttributeAttributableMapper;
import br.ufpe.cin.dsoa.monitor.MonitoredServiceListener;
import br.ufpe.cin.dsoa.monitor.MonitoringService;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.Util;

/**
 * This component is responsible for listening for the registration of services that are intended to be monitored (As
 * indicated by the monitored.service property.
 * 
 * @author fabions
 *
 */
public class MonitoredServiceListenerImpl implements MonitoredServiceListener{
	
	private MonitoringService monitoringService;
	
	/**
	 * This method is called when a is registered and it has a property with name monitored.service set.
	 * It is responsible for parsing the metrics that should be monitored (indicated through the service's properties)
	 * and starting monitoring them. To do that, it creates proxy service that intercepts requests and creates an 
	 * InvocationEvent that is sent to the EventProcessingService. There, there are Property Computing Agents that do the
	 * real metric computation. For each service that is monitored, the Monitoring Service also creates a MonitoredService
	 * that stores corresponding metrics and implements Monitorable interface (see OSGi spec).
	 */
	public void onArrival(ServiceReference reference) {
		Boolean isProxy = ((Boolean)reference.getProperty(Constants.SERVICE_PROXY)==null ? false : (Boolean)reference.getProperty(Constants.SERVICE_PROXY));
		if (!isProxy) {
			List<AttributeAttributableMapper> attributeAttributableMappers = new ArrayList<AttributeAttributableMapper>();
			AttributeAttributableMapper attributeAttributableMapper = null;
			String servicePid = Util.getPid(reference);
			String keys[] = reference.getPropertyKeys();
			for (String key : keys) {
				attributeAttributableMapper = AttributeParser.parse(servicePid, key);
				if (attributeAttributableMapper != null) {
					attributeAttributableMappers.add(attributeAttributableMapper);
				}
			}
			if (!attributeAttributableMappers.isEmpty()) {
				monitoringService.startMonitoring(reference, attributeAttributableMappers);
			}
		}
	}
	
	public void onDeparture(ServiceReference reference) {
		monitoringService.stopMonitoring(reference);
	}
}
