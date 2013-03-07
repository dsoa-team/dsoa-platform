package br.ufpe.cin.dsoa.epcenter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.epcenter.NotificationListener;
import br.ufpe.cin.dsoa.event.InvocationEvent;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

public class EventProcessingCenterImpl implements EventProcessingCenter {

	private EPServiceProvider epServiceProvider;
	private final List<String> eventNames;

	public EventProcessingCenterImpl(BundleContext context) {
		this();
	}
	
	public EventProcessingCenterImpl() {
		this.eventNames = new ArrayList<String>();
	}
	
	public void start() {
		this.epServiceProvider = EPServiceProviderManager.getProvider(
				"EngineInstance", new Configuration());
		this.configureEvents();
		this.configureContexts();
	}

	public void stop() {
		this.epServiceProvider.destroy();
	}
	
	private void configureEvents() {
		this.defineEvent(InvocationEvent.class);
	}
	
	private void configureContexts() {
		String serviceCtx = "create context service partition by service from InvocationEvent";
		String operationCtx = "create context operation partition by service,operationName from InvocationEvent";
		this.defineContext(serviceCtx);
		this.defineContext(operationCtx);
	}
	
	public void publishEvent(Object event) {
		this.epServiceProvider.getEPRuntime().sendEvent(event);
	}

	@SuppressWarnings("rawtypes")
	public void publishEvent(Map event, String eventName) {
		this.eventNames.add(eventName);
		this.epServiceProvider.getEPRuntime().sendEvent(event, eventName);
	}

	@SuppressWarnings("rawtypes")
	public void defineEvent(Class eventClass) {
		this.eventNames.add(eventClass.getSimpleName());
		this.epServiceProvider.getEPAdministrator().getConfiguration().addEventType(eventClass);
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
	
	/*public void subscribe(String statementName, EventConsumer eventConsumer) {
		EventNotifier notifier = notifierMap.get(statementName);
		
		if(notifier == null) {
			notifier = new EventNotifier();
			notifierMap.put(statementName, notifier);
			epServiceProvider.getEPAdministrator().getStatement(statementName).addListener(notifier);
		}
		
		notifier.addEventConsumer(eventConsumer);
	}*/
	
	public void subscribe(String subscription, final NotificationListener eventConsumer) {
		defineStatement("user", subscription);
		this.epServiceProvider.getEPAdministrator().getStatement("user").
			addListener(new EventNotifier(eventConsumer));
	}
	
	public void unsubscribe(String statementName, NotificationListener eventConsumer) {
		/*EventNotifier notifier = this.notifierMap.get(statementName);
		
		if(notifier != null) {
			notifier.removeEventConsumer(eventConsumer);
			if(!notifier.hasEventConsumers()) {
				epServiceProvider.getEPAdministrator().getStatement(statementName).removeListener(notifier);
				this.notifierMap.remove(statementName);
			}
		}*/
	}

	public void undefineEvents() {
		for (String eventName : this.eventNames) {
			this.undefineEvent(eventName);
		}
	}
	
	public void undefineEvent(Class eventClass) {
		this.undefineEvent(eventClass.getSimpleName());
	}
	
	public void undefineEvent(String eventName) {
		this.epServiceProvider.getEPAdministrator().getConfiguration().removeEventType(eventName, true);
	}
	
	public void defineContext(String ctxStatement) {
		this.epServiceProvider.getEPAdministrator().createEPL(ctxStatement);
	}
}
