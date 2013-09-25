package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBException;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventChannel;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.OutputTerminal;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.event.agent.InputEvent;
import br.ufpe.cin.dsoa.api.event.agent.OutputEvent;
import br.ufpe.cin.dsoa.api.event.agent.Processing;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingMapping;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingQuery;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.event.EventTypeCatalog;
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
	
	//EventTypeName/EventChannel
	private Map<String, EventChannel> channelMap = new HashMap<String, EventChannel>();

	//private Map<String, EventSubscriber> listenerMap = new HashMap<String, EventSubscriber>();

	private ConcurrentHashMap<String, EventConsumer> consumers = new ConcurrentHashMap<String, EventConsumer>();

	public EsperProcessingService(BundleContext ctx) {
		this.ctx = ctx;
	}

	// TODO REMOVE
	public EsperProcessingService() {
	}

	// TODO:REMOVER

	public void start() throws JAXBException {
		Configuration configuration = new Configuration();
		configuration.getEngineDefaults().getThreading().setInsertIntoDispatchPreserveOrder(false);
		configuration.getEngineDefaults().getThreading().setListenerDispatchPreserveOrder(false);
		
		this.epServiceProvider = EPServiceProviderManager.getProvider("Dsoa-EsperEngine",
				configuration);

		// defines invocation event
		Util.handlePlatformEventDefinitions(ctx.getBundle(), this.eventTypeCatalog, this);
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
			OutputEvent outputEvent = ((ProcessingMapping) processing).getOutputEvent();
			InputEvent inputEvent = ((ProcessingMapping) processing).getInputEvent();

			try {
				this.addOutputEventType(inputEvent, outputEvent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (EventTypeAlreadyCatalogedException e) {
				e.printStackTrace();
			}
		} else if (processing instanceof ProcessingQuery) {
			String id = agent.getId();
			String queryString = ((ProcessingQuery) agent.getProcessing()).getQuery();
			query = new Query(id, queryString);
			// XXX: processing query n esta sendo tratado (output events naosao registrados)
		}

		this.startQuery(query);
	}

	public synchronized void subscribe(final EventConsumer consumer, Subscription subscription) {
		Query query = null;
		QueryBuilder builder = this.getQueryBuilder(subscription);
		QueryDirector director = new QueryDirector(builder);
		director.construct();
		query = director.getQuery();
		//EventSubscriber subscriber = new EventSubscriber(consumer, subscription);
		EPStatement statement = this.startQuery(query);
		statement.addListener(new StatementAwareUpdateListener() {
			
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement,
					EPServiceProvider epServiceProvider) {
				
				for (EventBean e : newEvents) {
					Object event = e.getUnderlying();
					String eventTypeName = e.getEventType().getName();
					
					@SuppressWarnings("unchecked")
					Event dsoaEvent = convertEsperEvent(eventTypeName, (Map<String, Object>) event);
					consumer.handleEvent(dsoaEvent);
				}
			}
		});
		//statement.setSubscriber(subscriber);
		//this.listenerMap.put(consumer.getId(), subscriber);
	}
	
	private Event convertEsperEvent(String eventTypeName, Map<String, Object> esperEvent){
		
		Event dsoaEvent = null;

		Map<String, Object> metadata = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		for (String key : ((Map<String, Object>) esperEvent).keySet()) {
			if (key.startsWith("data_")) {
				String newKey = key.replace("data_", "");
				data.put(newKey, esperEvent.get(key));
			} else if (key.startsWith("metadata_")) {
				String newKey = key.replace("metadata_", "");
				metadata.put(newKey, esperEvent.get(key));
			}
		}
		EventType eventType = eventTypeCatalog.get(eventTypeName);
		dsoaEvent = eventType.createEvent(metadata, data);

		return dsoaEvent;
	}

	public void unsubscribe(EventConsumer consumer, Subscription subscription) {
		String statmentId = subscription.getId();
		EPStatement stmt = this.epServiceProvider.getEPAdministrator().getStatement(statmentId);
		if(stmt != null) {
			stmt.destroy();
		}
	}

	/**
	 * add event type on eventTypeMap and registers event definition on esper
	 */
	public void registerEventType(EventType eventType) {

		String eventTypeName = eventType.getName();
		
		if (this.eventTypeCatalog.contains(eventTypeName)) {
			Map<String, Object> definition = eventType.toDefinitionMap();
			this.registerEventTypeOnEsper(eventTypeName, definition);
		}
	}

	/**
	 * register new event on esper
	 * 
	 * @param eventTypeName
	 * @param definition
	 */
	private void registerEventTypeOnEsper(String eventTypeName, Map<String, Object> definition) {
		try {
			this.epServiceProvider.getEPAdministrator().getConfiguration()
					.addEventType(eventTypeName, definition);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error while configuring event: " + eventTypeName);
		}
	}

	private void createAgentContext() {
		String contextEPL = "create context " + Constants.CONTEXT_NAME
				+ " partition by metadata_source from InvocationEvent";
		this.epServiceProvider.getEPAdministrator().createEPL(contextEPL);
	}

	private void addOutputEventType(InputEvent inputEvent, OutputEvent outputEvent)
			throws ClassNotFoundException, EventTypeAlreadyCatalogedException {

		String inputEventTypeName = inputEvent.getType();

		EventType inputEventType = this.eventTypeCatalog.get(inputEventTypeName);

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
			this.eventTypeCatalog.remove(eventTypeName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected QueryBuilder getQueryBuilder(EventProcessingAgent agent) {
		return new EsperAgentBuilder(agent);
	}

	protected QueryBuilder getQueryBuilder(Subscription subscription) {
		return new EsperSubscriptionBuilder(subscription);
	}

	protected EPStatement startQuery(Query query) {
		
		String queryString  = query.getQueryString();
		String queryId = query.getId();
		
		EPStatement stmt = this.epServiceProvider.getEPAdministrator().createEPL(queryString, queryId);
		
		return stmt;
	}

	@Override
	public void unRegisterAgent(String agentId) {
		// TODO Auto-generated method stub

	}

	public EPServiceProvider getEpProvider() {
		return this.epServiceProvider;
	}

	@Override
	public EventChannel getEventChannel(EventType eventType) {
		OutputTerminal output = new OutputTerminalAdapter(this);
		
		EventChannel channel = this.channelMap.get(eventType.getName());
		
		if(channel == null){
			channel = new EventAdminChannel(ctx, eventType, output);
			this.channelMap.put(eventType.getName(), channel);
		}
		
		return channel;
	}
}
