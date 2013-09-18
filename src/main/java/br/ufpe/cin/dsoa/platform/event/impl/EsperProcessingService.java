package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.event.agent.InputEvent;
import br.ufpe.cin.dsoa.api.event.agent.OutputEvent;
import br.ufpe.cin.dsoa.api.event.agent.Processing;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingMapping;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingQuery;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

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
public class EsperProcessingService implements EventProcessingService {

	private BundleContext ctx;

	private EPServiceProvider epServiceProvider;

	private Map<String, EventType> eventTypeMap = new HashMap<String, EventType>();

	private Map<String, EventSubscriber> listenerMap = new HashMap<String, EventSubscriber>();

	private ConcurrentHashMap<String, EventConsumer> consumers = new ConcurrentHashMap<String, EventConsumer>();

	private ConcurrentHashMap<String, EventProcessingAgent> agents = new ConcurrentHashMap<String, EventProcessingAgent>();

	public EsperProcessingService(BundleContext ctx) {
		this.ctx = ctx;
	}

	// TODO REMOVE
	public EsperProcessingService() {
	}

	public void start() {
		this.epServiceProvider = EPServiceProviderManager.getProvider(
				"Dsoa-EsperEngine", new Configuration());
	}

	public void stop() {
		this.epServiceProvider.destroy();
	}

	public void publish(Event event) {
		String name = event.getEventType().getName();
		Map<String, Object> eventMap = event.toMap();
		this.epServiceProvider.getEPRuntime().sendEvent(eventMap, name);
	}	
	
	public void registerAgent(EventProcessingAgent agent) {
		String id = null;
		String queryString = null;
		Query query = null;
		Processing processing = agent.getProcessing();

		if (processing instanceof ProcessingMapping) {
			QueryBuilder builder = this.getQueryBuilder(agent);
			QueryDirector director = new QueryDirector(builder);
			director.construct();
			query = director.getQuery();
			OutputEvent outputEvent = ((ProcessingMapping) processing)
					.getOutputEvent();
			InputEvent inputEvent = ((ProcessingMapping) processing)
					.getInputEvent();

			try {
				this.addOutputEventType(inputEvent, outputEvent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if (processing instanceof ProcessingQuery) {
			id = agent.getId();
			queryString = ((ProcessingQuery) agent.getProcessing()).getQuery();
			query = new Query(id, queryString);
			// XXX: processing query n esta sendo tratado (output events nao
			// sao registrados
		}

		boolean added = this.register(agent.getId(), agent, this.agents);
		if (added) {
			this.startQuery(query);
		}

		/*
		 * EventType eventType = agent.getOutputEventType();
		 * this.registerEventType(eventType);
		 */
	}
	
	public synchronized void subscribe(EventConsumer consumer, Subscription subscription) {
		Query query = null;
		QueryBuilder builder = this.getQueryBuilder(subscription);
		QueryDirector director = new QueryDirector(builder);
		director.construct();
		query = director.getQuery();
		EventSubscriber listener = new EventSubscriber(consumer, subscription);
		EPStatement statement = this.startQuery(query);
		statement.setSubscriber(listener);
		this.listenerMap.put(consumer.getId(), listener);
	}

	public void unsubscribe(EventConsumer consumer, Subscription subscription) {

	}

	/**
	 * add event type on eventTypeMap and registers event definition on esper
	 */
	public void registerEventType(EventType eventType) {
		String eventTypeName = eventType.getName();
		if (!eventTypeMap.containsKey(eventTypeName)) {
			Map<String, Object> definition = eventType.toDefinitionMap();
			try {
				this.epServiceProvider.getEPAdministrator().getConfiguration()
						.addEventType(eventTypeName, definition);
				this.eventTypeMap.put(eventTypeName, eventType);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Error while configuring event: "
						+ eventTypeName);
			}
		}
	}

	private void addOutputEventType(InputEvent inputEvent,
			OutputEvent outputEvent) throws ClassNotFoundException {

		String inputEventTypeName = inputEvent.getType();

		EventType inputEventType = this.eventTypeMap.get(inputEventTypeName);

		String name = outputEvent.getType();
		List<PropertyType> metadata = outputEvent.getMetadata();
		List<PropertyType> data = outputEvent.getData();

		if (null != inputEvent) {
			if (null != metadata) {
				Iterator<PropertyType> iterator = metadata.iterator();

				while (iterator.hasNext()) {
					PropertyType propertyType = iterator.next();
					String typeName = propertyType.getTypeName();

					if (typeName == null) {
						String key = propertyType.getExpression().replace(
								inputEvent.getAlias() + ".metadata.", "");

						PropertyType rawPropertyType = inputEventType.getMetadataPropertyType(key);
						typeName = rawPropertyType.getTypeName();
					}
					propertyType.setClazz(Class.forName(typeName));
				}
			}

			if (null != data) {
				Iterator<PropertyType> iterator = data.iterator();

				while (iterator.hasNext()) {
					PropertyType propertyType = iterator.next();
					String typeName = propertyType.getTypeName();

					if (typeName == null) {
						String key = propertyType.getExpression().replace(
								inputEvent.getAlias() + ".data.", "");

						PropertyType rawPropertyType = inputEventType.getDataPropertyType(key);
						typeName = rawPropertyType.getTypeName();
					}
					propertyType.setClazz(Class.forName(typeName));
				}
			}
		}

		EventType eventType = new EventType(name, metadata, data);
		String eventTypeName = eventType.getName();

		boolean registered = eventTypeMap.containsKey(eventTypeName);
		if (!registered) {
			this.eventTypeMap.put(eventTypeName, eventType);
		}
	}

	public void unregisterEventType(EventType eventType) {
		String eventTypeName = eventType.getName();
		if (eventTypeMap.containsKey(eventTypeName)) {
			this.removeEventType(eventTypeName);
		}
	}

	private void removeEventType(String eventTypeName) {
		try {
			this.epServiceProvider.getEPAdministrator().getConfiguration()
					.removeEventType(eventTypeName, true);
			this.eventTypeMap.remove(eventTypeName);
		} catch (Exception e) {
			e.printStackTrace();
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

	protected QueryBuilder getQueryBuilder(EventProcessingAgent agent) {
		return new EsperAgentBuilder(agent);
	}
	
	protected QueryBuilder getQueryBuilder(Subscription subscription) {
		return new EsperSubscriptionBuilder(subscription);
	}

	protected EPStatement startQuery(Query query) {
		return this.epServiceProvider.getEPAdministrator().createEPL(
				query.getQueryString(), query.getId());
	}
	
	@Override
	public void unRegisterAgent(String agentId) {
		// TODO Auto-generated method stub

	}

	private <T> boolean register(String subjectId, T subject,
			ConcurrentHashMap<String, T> map) {
		T stored = map.putIfAbsent(subjectId, subject);
		return (null == stored) ? true : false;
	}

	public EPServiceProvider getEpProvider() {
		return this.epServiceProvider;
	}
}
