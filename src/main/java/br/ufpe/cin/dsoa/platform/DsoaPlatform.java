package br.ufpe.cin.dsoa.platform;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.event.AgentCatalog;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.platform.handler.dependency.manager.Analyzer;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringService;
import br.ufpe.cin.dsoa.platform.registry.ServiceRegistry;

public class DsoaPlatform {

	private BundleContext context;

	private ServiceRegistry serviceRegistry;
	
	private AttributeCatalog attributeCatalog;
	
	private AttributeEventMapperCatalog attEventMapperCatalog;
	
	private EventTypeCatalog eventTypeCatalog;
	
	private AgentCatalog agentCatalog;
	
	private MonitoringService monitoringService;
	
	private EventProcessingService epService;
	
	private Analyzer analizer;

	public DsoaPlatform(BundleContext context) {
		this.context = context;
	}
	
	
	public BundleContext getContext() {
		return context;
	}
	
	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public AttributeCatalog getAttributeCatalog() {
		return attributeCatalog;
	}

	public AttributeEventMapperCatalog getAttEventMapperCatalog() {
		return attEventMapperCatalog;
	}

	public EventTypeCatalog getEventTypeCatalog() {
		return eventTypeCatalog;
	}

	public AgentCatalog getAgentCatalog() {
		return agentCatalog;
	}

	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public EventProcessingService getEpService() {
		return epService;
	}


	public Analyzer getAnalyzer() {
		return analizer;
	}
	
	
}
