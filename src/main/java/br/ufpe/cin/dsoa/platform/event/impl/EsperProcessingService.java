package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

/**
 * Esper implementation of EventProcessingService. This component implements
 * DSOA event processing capability. It receives events from Event Producers
 * through Event Channels. These events comprise streams that are connected
 * through Event Processing Agents. These components filter, aggregate, and
 * transform events generating new Event Streams that can be forwarded to Event
 * Consumers that subscribe themselves to listen those events. The collection of
 * Event Producers, Event Processing Agents, and Event Consumers connected
 * together compose Event Processing Networks.
 * 
 * @author fabions
 * 
 */
public class EsperProcessingService extends StreamProcessingService implements
		EventProcessingService {

	private BundleContext ctx;
	
	private EPServiceProvider epServiceProvider;
	
	private Map<String, EventType> eventTypeMap = new HashMap<String, EventType>();

	private Map<String, EventListener> listenerMap = new HashMap<String, EventListener>();

	
	public EsperProcessingService(BundleContext ctx) {
		this.ctx = ctx;
	}
	
	//TODO REMOVE
	public EsperProcessingService(EPServiceProvider esperProvider) {
		// TODO Auto-generated constructor stub
	}

	public void start() {
		this.epServiceProvider = EPServiceProviderManager.getProvider("Dsoa-EsperEngine", new Configuration());
	}

	public void stop() {
		this.epServiceProvider.destroy();
	}
	
	public void publish(Event event) {
		String name = event.getEventType().getName();
		Map<String, Object> eventMap = event.toMap();
		this.epServiceProvider.getEPRuntime().sendEvent(eventMap, name);
	}	
	
	public synchronized void subscribe(EventConsumer consumer, Subscription subscription) {
		String eventTypeName = subscription.getEventTypeName();
		EventType eventType = this.eventTypeMap.get(eventTypeName);
		if (eventType != null && eventType.isValid(subscription.getFilter())) {
			EventListener listener = new EventListener(ctx, consumer, subscription);
			listener.start();
			this.listenerMap.put(consumer.getId(), listener);
		}
	}
	
	public void unsubscribe(EventConsumer consumer, Subscription subscription) {
		
	}
	
	public void registerEventType(EventType eventType) {
		String eventTypeName = eventType.getName();
		if (!eventTypeMap.containsKey(eventTypeName)) {
			Map<String, Object> definition = eventType.toMap();
			try {
				this.epServiceProvider.getEPAdministrator().getConfiguration()
						.addEventType(eventTypeName, definition);
				this.eventTypeMap.put(eventTypeName, eventType);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Error while configuring event: " + eventTypeName);
			}
		}
	}

	public void unregisterEventType(EventType eventType) {
		String eventTypeName = eventType.getName();
		if (eventTypeMap.containsKey(eventTypeName)) {
			try {
				this.epServiceProvider.getEPAdministrator().getConfiguration()
						.removeEventType(eventTypeName, true);
				this.eventTypeMap.remove(eventTypeName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public EventType getEventType(String eventTypeName) {
		return this.eventTypeMap.get(eventTypeName);
	}
	
	@Override
	public List<EventType> getEventTypes() {
		return new ArrayList<EventType>(this.eventTypeMap.values());
	}

	@Override
	protected QueryBuilder getQueryBuilder(EventProcessingAgent agent) {
		return new EsperAgentBuilder(agent);
	}

	@Override
	protected void startQuery(Query query) {
		this.epServiceProvider.getEPAdministrator().createEPL(
				query.getQueryString(), query.getId());
	}

	@Override
	public void unRegisterAgent(String agentId) {
		// TODO Auto-generated method stub
		
	}

}
