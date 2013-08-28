package br.ufpe.cin.dsoa.platform.configurator;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.service.Service;
import br.ufpe.cin.dsoa.service.impl.OsgiService;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.Util;

/**
 * 
 * This component is responsible for listening for the registration of services
 * that are intended to be monitored (As indicated by the monitored.service
 * property). For each service that is registered, it creates and maintains a
 * "monitored service", which is a service that maintains metrics related to a
 * corresponding business service.
 * 
 * @author fabions
 * 
 */
public class DsoaServiceTracker implements ServiceTrackerCustomizer {

	private MonitoringService monitoringService;

	public DsoaServiceTracker(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	/**
	 * This method is called when a service is registered and it has a property with
	 * name monitored.service set. It is responsible for parsing the metrics
	 * that should be monitored (indicated through the service's properties) and
	 * starting monitoring them. To do that, it creates proxy service that
	 * intercepts requests and creates an InvocationEvent that is sent to the
	 * EventProcessingService. There, there are Property Computing Agents that
	 * do the real metric computation. For each service that is monitored, the
	 * Monitoring Service also creates a ServiceMonitor that stores
	 * corresponding metrics and implements Monitorable interface (see OSGi
	 * spec).
	 */
	public Object addingService(ServiceReference reference) {
		Boolean isProxy = ((Boolean) reference.getProperty(Constants.SERVICE_PROXY) == null ? false : Boolean
				.valueOf(reference.getProperty(Constants.SERVICE_PROXY).toString()));
		Object tracked = null;
		if (!isProxy) {
			Service service = new OsgiService(reference);
			//this.registry.addService(service);
			tracked = monitoringService.startMonitoring(service);
		}
		return tracked;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		// TODO Auto-generated method stub
	}

	public void removedService(ServiceReference reference, Object service) {
		String serviceId = Util.getId(reference);
		monitoringService.stopMonitoring(serviceId);
	}

	public void setMonitoringService(MonitoringService monitoringServiceImpl) {
		this.monitoringService = monitoringServiceImpl;
	}

}
