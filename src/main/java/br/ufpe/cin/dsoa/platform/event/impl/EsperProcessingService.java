package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventAdapter;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.event.agent.InputEvent;
import br.ufpe.cin.dsoa.api.event.agent.OutputEvent;
import br.ufpe.cin.dsoa.api.event.agent.Processing;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingMapping;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingQuery;
import br.ufpe.cin.dsoa.platform.event.AgentCatalog;
import br.ufpe.cin.dsoa.platform.event.EventAdapterCatalog;
import br.ufpe.cin.dsoa.platform.event.EventDistribuitionService;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.Util;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

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

	private EventTypeCatalog eventTypeCatalog;

	private EventAdapterCatalog eventAdapterCatalog;
	
	private AgentCatalog agentCatalog;

	private EventDistribuitionService eventDistribuitionService;
	
	public EsperProcessingService(BundleContext ctx) {
		this.ctx = ctx;
	}

	public void start() throws JAXBException {
		Configuration configuration = new Configuration();
		configuration.getEngineDefaults().getThreading()
				.setInsertIntoDispatchPreserveOrder(false);
		configuration.getEngineDefaults().getThreading()
				.setListenerDispatchPreserveOrder(false);

		this.epServiceProvider = EPServiceProviderManager.getProvider(
				"Dsoa-EsperEngine", configuration);

		// defines invocation event
		EventTypeList primitiveEvents = Util.handlePlatformEventDefinitions(
				ctx.getBundle(), this.eventTypeCatalog, this);
		if (primitiveEvents != null && primitiveEvents.getEvents() != null) {
			for (EventType primitiveEvent : primitiveEvents.getEvents()) {
				this.registerEventDistributionListener(primitiveEvent);
			}
		}
		this.createAgentContext();
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
			} catch (EventTypeAlreadyCatalogedException e) {
				e.printStackTrace();
			}
		} else if (processing instanceof ProcessingQuery) {
			String id = agent.getId();
			String queryString = ((ProcessingQuery) agent.getProcessing())
					.getQuery();
			query = new Query(id, queryString);
			// XXX: processing query n esta sendo tratado (output events nao sao
			// registrados)
		}

		System.out.println("QUERY AGENT: " + query.getQueryString());

		this.startQuery(query);
	}
	
	/**
	 *  subscribe to an event type  
	 */
	public synchronized void subscribe(final EventConsumer consumer,
			Subscription subscription) {
		
		this.subscribe(consumer, subscription, true);
	}

	/**
	 * subscribe to an event type. If shared = false
	 * that subscription considers events related to one event consumer.
	 */
	public synchronized void subscribe(final EventConsumer consumer,
			Subscription subscription, boolean shared) {

		Query query = null;
		QueryBuilder builder = this.getQueryBuilder(consumer, subscription,
				shared);
		QueryDirector director = new QueryDirector(builder);
		director.construct();
		query = director.getQuery();

		//TODO REMOVER
		System.out.println("QUERY CLIENT: " + query.getQueryString());

		EPStatement statement = this.startQuery(query);
		statement.addListener(new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents,
					EPStatement statement, EPServiceProvider epServiceProvider) {

				for (EventBean e : newEvents) {
					Object event = e.getUnderlying();
					String eventTypeName = e.getEventType().getName();

					@SuppressWarnings("unchecked")
					Event dsoaEvent = convertEsperEvent(eventTypeName,
							(Map<String, Object>) event);
					consumer.handleEvent(dsoaEvent);
				}
			}
		});
	}

	private Event convertEsperEvent(String eventTypeName,
			Map<String, Object> esperEvent) {

		EventType eventType = eventTypeCatalog.get(eventTypeName);

		if (eventType == null) {
			String typeName = (String) esperEvent.get(Constants.EVENT_METADATA
					+ Constants.UNDERLINE + Constants.EVENT_TYPE);
			eventType = eventTypeCatalog.get(typeName);
		}

		Event dsoaEvent = eventType.createEvent(esperEvent);

		return dsoaEvent;
	}

	public void unsubscribe(EventConsumer consumer, Subscription subscription) {
		String statmentId = subscription.getId();
		destroyStatement(statmentId);
	}

	/**
	 * add event type on eventTypeMap and registers event definition on esper
	 */
	public void registerEventType(final EventType eventType) {

		String eventTypeName = eventType.getName();

		if (this.eventTypeCatalog.contains(eventTypeName)) {
			Map<String, Object> definition = eventType.toDefinitionMap();
			this.registerEventTypeOnEsper(eventTypeName, definition);

			// caso ninguem tenha interesse em tais eventos
			// esses eventos sao lancados sem necessidade
			// (isso sera tratado como uma subscricao)
			if (!eventType.isPrimitive()) {
				
				//XXX: TEMP
				Collection<EventAdapter> adapters = this.eventAdapterCatalog.getAdapters();
				if(!adapters.isEmpty()) {
					Map<String, Object> config = new HashMap<String, Object>();

					for(EventAdapter adapter : adapters) {
						
						config.put(Constants.ADAPTER_ID, adapter.getId());
						this.eventDistribuitionService.exportEvents(eventType, config);
						this.eventDistribuitionService.importEvents(eventType, config);
					}
				}
				//XXX: TEMP
				
				
				this.subscribe(new EventConsumer() {

					@Override
					public void handleEvent(Event event) {
						eventDistribuitionService.postEvent(event);
					}

					@Override
					public String getId() {
						return eventType.getName();
					}
				}, new Subscription(eventType, null), true);
			}
		}
	}

	private void registerEventDistributionListener(final EventType eventType) {
		
		this.eventDistribuitionService.subscribe(new EventConsumer() {
			
			@Override
			public void handleEvent(Event event) {
				publish(event);
			}
			
			@Override
			public String getId() {
				return String.format("dsoa-%s", eventType.getName());
			}
		}, eventType);
	}

	/**
	 * register new event on esper
	 * 
	 * @param eventTypeName
	 * @param definition
	 */
	private void registerEventTypeOnEsper(String eventTypeName,
			Map<String, Object> definition) {
		try {
			this.epServiceProvider.getEPAdministrator().getConfiguration()
					.addEventType(eventTypeName, definition);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error while configuring event: "
					+ eventTypeName);
		}
	}

	private void createAgentContext() {
		String contextEPL = "create context " + Constants.CONTEXT_NAME
				+ " partition by metadata_source from InvocationEvent";
		this.epServiceProvider.getEPAdministrator().createEPL(contextEPL);
	}

	private void addOutputEventType(InputEvent inputEvent,
			OutputEvent outputEvent) throws ClassNotFoundException,
			EventTypeAlreadyCatalogedException {

		String inputEventTypeName = inputEvent.getType();

		EventType inputEventType = this.eventTypeCatalog
				.get(inputEventTypeName);

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

						PropertyType rawPropertyType = inputEventType
								.getMetadataPropertyType(key);
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

						PropertyType rawPropertyType = inputEventType
								.getDataPropertyType(key);
						typeName = rawPropertyType.getTypeName();
					}
					propertyType.setClazz(Class.forName(typeName));
				}
			}
		}

		EventType eventType = new EventType(name, metadata, data);
		String eventTypeName = eventType.getName();

		boolean registered = this.eventTypeCatalog.contains(eventTypeName);
		if (!registered) {
			this.eventTypeCatalog.add(eventType);
		}
	}

	public void unregisterEventType(EventType eventType) {
		String eventTypeName = eventType.getName();
		if (this.eventTypeCatalog.contains(eventTypeName)) {
			this.removeEventType(eventTypeName);
		}
	}

	private void removeEventType(String eventTypeName) {
		try {
			this.epServiceProvider.getEPAdministrator().getConfiguration()
					.removeEventType(eventTypeName, true);
			this.eventTypeCatalog.remove(eventTypeName);// FIXME: move to out
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected QueryBuilder getQueryBuilder(EventProcessingAgent agent) {
		return new EsperAgentBuilder(agent);
	}

	/**
	 * 
	 * @param eventConsumer
	 * @param subscription
	 * @param shared
	 *            - use data from all consumers or specific ones
	 * @return
	 */
	protected QueryBuilder getQueryBuilder(EventConsumer eventConsumer,
			Subscription subscription, boolean shared) {

		QueryBuilder builder = null;

		if (shared) {
			builder = new EsperSharedSubscriptionBuilder(subscription);
		} else {
			EventType outputEventType = subscription.getEventType();
			EventProcessingAgent eventProcessingAgent = this.agentCatalog
					.getAgent(outputEventType);
			builder = new EsperSubscriptionBuilder(eventConsumer, subscription,
					eventProcessingAgent);
		}

		return builder;
	}

	protected EPStatement startQuery(Query query) {

		String queryString = query.getQueryString();
		String queryId = query.getId();
		EPStatement stmt = this.epServiceProvider.getEPAdministrator()
				.createEPL(queryString, queryId);

		return stmt;
	}

	@Override
	public void unRegisterAgent(String agentId) {
		destroyStatement(agentId);
	}

	public EPServiceProvider getEpProvider() {
		return this.epServiceProvider;
	}

	private void destroyStatement(String stmtId) {
		EPStatement stmt = this.epServiceProvider.getEPAdministrator()
				.getStatement(stmtId);
		if (stmt != null) {
			stmt.destroy();
		}
	}
}
