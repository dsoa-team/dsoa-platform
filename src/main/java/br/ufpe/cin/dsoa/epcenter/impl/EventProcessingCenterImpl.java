package br.ufpe.cin.dsoa.epcenter.impl;

import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

public class EventProcessingCenterImpl implements EventProcessingCenter {

	private final EPServiceProvider epServiceProvider;
	private final BundleContext bundleContext;

	public EventProcessingCenterImpl(BundleContext context) {
		this.bundleContext = context;
		this.epServiceProvider = EPServiceProviderManager.getProvider(
				"EngineInstance", new Configuration());
	}

	public void publishEvent(Object event) {
		this.epServiceProvider.getEPRuntime().sendEvent(event);
	}

	@SuppressWarnings("rawtypes")
	public void publishEvent(Map event, String eventName) {
		this.epServiceProvider.getEPRuntime().sendEvent(event, eventName);
	}

	@SuppressWarnings("rawtypes")
	public void defineEvent(Class event) {
		this.epServiceProvider.getEPAdministrator().getConfiguration().addEventType(event);
	}
	
	public void defineStatement(String name, String statement) {
		this.epServiceProvider.getEPAdministrator().createEPL(statement, name);
	}
	
	public void defineStatement(String name, String statement, List<String> userObject) {
		this.epServiceProvider.getEPAdministrator().createEPL(statement, name, userObject);
	}

}
