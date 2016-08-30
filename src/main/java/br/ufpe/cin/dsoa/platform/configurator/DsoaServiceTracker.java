package br.ufpe.cin.dsoa.platform.configurator;

import java.util.List;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.api.service.impl.OsgiService;
import br.ufpe.cin.dsoa.platform.resource.ResourceManager;
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

	private ResourceManager resourceManagerImpl;

	public DsoaServiceTracker(ResourceManager resourceManagerImpl) {
		this.resourceManagerImpl = resourceManagerImpl;
	}

	/**
	 * This method is called when a service is registered and it has a property
	 * with name service.managed set. It is responsible for parsing the
	 * metrics that should be monitored (indicated through the service's
	 * properties) and starting monitoring them. To do that, it creates proxy
	 * service that intercepts requests and creates an InvocationEvent that is
	 * sent to the EventProcessingService. There, there are Property Computing
	 * Agents that do the real metric computation. For each service that is
	 * monitored, the Monitoring Service also creates a MonitoredService that
	 * stores corresponding metrics and implements Monitorable interface (see
	 * OSGi spec).
	 */
	public Object addingService(ServiceReference reference) {
		Boolean isProxy = ((Boolean) reference.getProperty(Constants.SERVICE_PROXY) == null ? false : Boolean
				.valueOf(reference.getProperty(Constants.SERVICE_PROXY).toString()));
		if (!isProxy) {
			try {
				List<OsgiService> services = OsgiService.getOsgiServices(reference);
				for (Service service : services) {
					resourceManagerImpl.manage(service);
				}
			} catch (ClassNotFoundException e) {
				// this should never happen
				e.printStackTrace();
			}
		}
		return reference;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		// TODO Auto-generated method stub
	}

	public void removedService(ServiceReference reference, Object service) {
		String serviceId = Util.getId(reference);
		resourceManagerImpl.release(serviceId);
	}

	public void setMonitoringService(ResourceManager resourceManagerImpl) {
		this.resourceManagerImpl = resourceManagerImpl;
	}

}
