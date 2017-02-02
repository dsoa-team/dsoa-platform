package br.ufpe.cin.dsoa.platform.configurator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.platform.registry.impl.DsoaOsgiUtils;
import br.ufpe.cin.dsoa.platform.resource.ManagedService;
import br.ufpe.cin.dsoa.platform.resource.ResourceManager;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.DsoaUtil;

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
				List<ServiceInstance> services = DsoaOsgiUtils.translateOsgiServiceToDsoa(reference);
				List<ManagedService> managedServices = new ArrayList<ManagedService>();
				// MODIFICAR PARA REFERENCIAR SERVICE INSTANCE
				for (ServiceInstance service : services) {
					managedServices.add(resourceManagerImpl.manage(service));
				}
				return managedServices;
			} catch (ClassNotFoundException e) {
				// this should never happen
				e.printStackTrace();
			}
		}
		return null;
	}

	public void modifiedService(ServiceReference reference, Object services) {
/*		if (services != null && services instanceof List<?>) {
			for (ManagedService mServ : (List<ManagedService>)services) {
				Boolean started = (Boolean)reference.getProperty("service.started");
				if (started) {
					mServ.start();
				}
			}
		}*/
	}

	public void removedService(ServiceReference reference, Object service) {
		String serviceId = DsoaUtil.getId(reference);
		resourceManagerImpl.release(serviceId);
	}

	public void setMonitoringService(ResourceManager resourceManagerImpl) {
		this.resourceManagerImpl = resourceManagerImpl;
	}

}
