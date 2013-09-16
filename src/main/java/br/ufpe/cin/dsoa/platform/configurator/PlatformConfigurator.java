package br.ufpe.cin.dsoa.platform.configurator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.event.AgentCatalog;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.util.Constants;


/**
 * 	This component is responsible for configuring the components that listen for platform extensions, they are: 1. 
 	DsoaBundleListener that looks for new attributes, events, and agents; 2. DsoaServiceTracker that listens service
	registrations and, when a remote service is registered, triggers the MonitoringService to start service monitoring.
	
 * @author fabions
 *
 */
public class PlatformConfigurator {

	private BundleContext 				context;
	
	private DsoaBundleListener 			listener;
	private ServiceTracker				tracker;
	
	/** 
	 * These catalog services are necessary in order to configure the DsoaBundleListner
	 * that listens for platform extensions.
	 */
	private AttributeCatalog 			attributeCatalog;
	private AttributeEventMapperCatalog attributeEventMapperCatalog;
	private AgentCatalog 				agentCatalog;
	
	private EventProcessingService		epService;
	
	/**
	 * This service is necessary for configuring the tracker that listens for
	 * service registration and registers corresponding monitors.
	 */
	private MonitoringService 			monitoringService;

	public PlatformConfigurator(BundleContext context) {
		this.context = context;
	}

	public void start() {
		Filter managedFilter = null;
		try {
			managedFilter = context.createFilter(Constants.MANAGED_SERVICE);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			throw new IllegalStateException("Internal error configuring DSOA Platform: ");
		}
		this.configurePrimitiveEvents();
		
		tracker = new ServiceTracker(context, managedFilter, new DsoaServiceTracker(monitoringService));
		tracker.open();
		
		listener = new DsoaBundleListener(this.context);
		listener.setEventProcessingService(this.epService);
		listener.setAttributeCatalog(this.attributeCatalog);
		listener.setAttributeEventMapperCatalog(this.attributeEventMapperCatalog);
		listener.setAgentCatalog(this.agentCatalog);
		listener.open();
	}

	private void configurePrimitiveEvents() {
/*		List<PropertyType> metadata = new ArrayList<PropertyType>();
		List<PropertyType> data = new ArrayList<PropertyType>();

		//add metadata
		metadata.add(new PropertyType("id", String.class, true));
		metadata.add(new PropertyType("timestamp", Long.class, true));
		
		//add data
		data.add(new PropertyType("consumerId", String.class, false));
		data.add(new PropertyType("serviceId", String.class, true));
		data.add(new PropertyType("operationName", String.class, true));
		data.add(new PropertyType("requestTimestamp", Long.class, true));
		data.add(new PropertyType("responseTimestamp", Long.class, true));
		data.add(new PropertyType("parameterTypes", Class[].class,	true));
		data.add(new PropertyType("parameterValues", Object[].class, true));
		data.add(new PropertyType("returnType", Class.class, true));
		data.add( new PropertyType("returnValue", Object.class, true));
		data.add(new PropertyType("success", Boolean.class, true));
		data.add(new PropertyType("exception", Exception.class, false));
		
		EventType invocationEventType = new EventType(Constants.INVOCATION_EVENT, metadata, data);
		epService.registerEventType(invocationEventType);*/
	}

	public void stop() throws Exception {
		tracker.close();
		listener.close();
	}
}
