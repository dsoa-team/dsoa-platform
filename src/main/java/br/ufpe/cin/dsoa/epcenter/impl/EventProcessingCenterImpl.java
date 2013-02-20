package br.ufpe.cin.dsoa.epcenter.impl;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.epcenter.EventConsumer;
import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

public class EventProcessingCenterImpl implements EventProcessingCenter {

	private final EPServiceProvider epServiceProvider;
	private final Map<String, EventNotifier> notifierMap;

	public EventProcessingCenterImpl(BundleContext context) {
		this.notifierMap = new Hashtable<String, EventNotifier>();
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

	public void defineEvent(String eventName, Map<String, Object> eventProperties) {
		this.epServiceProvider.getEPAdministrator().getConfiguration().
			addEventType(eventName, eventProperties);
		
	}
	
	public void subscribe(String statementName, EventConsumer eventConsumer) {
		EventNotifier notifier = notifierMap.get(statementName);
		
		if(notifier == null) {
			notifier = new EventNotifier();
			notifierMap.put(statementName, notifier);
			epServiceProvider.getEPAdministrator().getStatement(statementName).addListener(notifier);
		}
		
		notifier.addEventConsumer(eventConsumer);
	}
	
	public void unsubscribe(String statementName, EventConsumer eventConsumer) {
		EventNotifier notifier = this.notifierMap.get(statementName);
		
		if(notifier != null) {
			notifier.removeEventConsumer(eventConsumer);
			if(!notifier.hasEventConsumers()) {
				epServiceProvider.getEPAdministrator().getStatement(statementName).removeListener(notifier);
				this.notifierMap.remove(statementName);
			}
		}
	}
}
