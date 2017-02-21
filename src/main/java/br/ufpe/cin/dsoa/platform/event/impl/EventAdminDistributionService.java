package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventAdapter;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventDistribuitionService;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.platform.event.EventAdapterCatalog;
import br.ufpe.cin.dsoa.util.Constants;

public class EventAdminDistributionService implements EventDistribuitionService {

	private ExecutorService executorService;
	private EventTypeCatalog eventTypeCatalog;
	private EventAdapterCatalog eventAdapterCatalog;

	private BundleContext context;
	private ServiceTracker adapterTracker;
	private EventAdmin eventAdmin;

	public EventAdminDistributionService(BundleContext context) {
		this.context = context;
	}

	public void start() {
		//this.executorService = Executors.newFixedThreadPool(20,Executors.defaultThreadFactory());

/*		this.adapterTracker = new ServiceTracker(this.context, EventAdapter.class.getName(),
				new AdapterCustomizer());
		this.adapterTracker.open();*/
	}

	public void stop() {
/*		this.executorService.shutdown();
		this.adapterTracker.close();*/
	}

	@Override
	public void postEvent(String eventTypeName, Map<String, Object> metadata,
			Map<String, Object> data) {
/*		NotificationWorker worker = new NotificationWorker(eventTypeName, metadata, data);
		executorService.execute(worker);*/
	}

	@Override
	public void postEvent(Event event) {

/*		NotificationWorker worker = new NotificationWorker(event);
		executorService.execute(worker);*/
	}

	@SuppressWarnings("unchecked")
	@Override
	public void subscribe(final EventConsumer consumer, final EventType eventType) {

/*		final String topic = String.format("%s/*", eventType.getName());

		@SuppressWarnings("rawtypes")
		Hashtable props = new Hashtable();
		props.put(EventConstants.EVENT_TOPIC, new String[] { topic });
		context.registerService(EventHandler.class.getName(), new EventHandler() {

			@Override
			public void handleEvent(org.osgi.service.event.Event event) {
				Map<String, Object> rawEvent = (Map<String, Object>) event.getProperty(eventType
						.getName());
				Event dsoaEvent = eventType.createEvent(rawEvent);
				consumer.handleEvent(dsoaEvent);
			}
		}, props);*/
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void subscribe(final EventConsumer consumer, final EventType eventType,
			Map<String, Object> configuration) {
/*		
		final String topic = String.format("%s/*", eventType.getName());

		@SuppressWarnings("rawtypes")
		Hashtable props = new Hashtable();
		props.put(EventConstants.EVENT_TOPIC, new String[] { topic });
		
		//TODO: embutir mapa de filtros no mapa configuration (deixar o configuration mais generico) 
		//filter remote
		if(configuration.containsKey(Constants.REMOTE)){
			String filter = String.format("(%s=%s)", Constants.REMOTE, configuration.get(Constants.REMOTE));
			props.put(EventConstants.EVENT_FILTER, filter);
		}

		context.registerService(EventHandler.class.getName(), new EventHandler() {

			@Override
			public void handleEvent(org.osgi.service.event.Event event) {
				Map<String, Object> rawEvent = (Map<String, Object>) event.getProperty(eventType
						.getName());
				Event dsoaEvent = eventType.createEvent(rawEvent);
				consumer.handleEvent(dsoaEvent);
			}
		}, props);*/
	}
	

	@Override
	public void importEvents(final EventType eventType, Map<String, Object> configuration) {

/*		String adapterId = (String) configuration.get("adapter-id");
		final EventAdapter adapter = eventAdapterCatalog.get(adapterId);
		
		if(adapter == null){
			System.err.println("Adapter not found.");
			return;
		}

		Subscription subscription = new Subscription(eventType, null);

		adapter.importEvent(new EventConsumer() {

			@Override
			public void handleEvent(Event event) {
				event.setRemote();
				//postEvent(event);
			}

			@Override
			public String getComponentInstanceName() {
				return String.format("import-%s-from-%s", eventType.getName(), adapter.getId());
			}
		}, subscription);*/
	}

	@Override
	public void exportEvents(final EventType eventType, final Map<String, Object> configuration) {

/*		String adapterId = (String) configuration.get("adapter-id");
		final EventAdapter adapter = eventAdapterCatalog.get(adapterId);
		if(adapter == null){
			System.err.println("Adapter not found.");
			return;
		}
		
		subscribe(new EventConsumer() {

			public void handleEvent(Event event) {
				if(!event.isRemote()){
					adapter.exportEvent(event, configuration);
				}
			}

			public String getComponentInstanceName() {
				return String.format("export-%s-to-%s", eventType.getName(), adapter.getId());
			}
		}, eventType);*/

	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////Thread responsible to fire events to queue infrastructure
	// ////////////////////////////////////////////////////////////////////////////////////////
	class NotificationWorker implements Runnable {

		private String eventTypeName;
		private Map<String, Object> metadata;
		private Map<String, Object> data;

		private Event dsoaEvent;

		public NotificationWorker(String eventTypeName, Map<String, Object> metadata,
				Map<String, Object> data) {
			super();
			this.eventTypeName = eventTypeName;
			this.metadata = metadata;
			this.data = data;
		}

		public NotificationWorker(Event dsoaEvent) {
			this.dsoaEvent = dsoaEvent;
		}

		@Override
		public void run() {

			if (dsoaEvent == null) {
				EventType eventType = eventTypeCatalog.get(eventTypeName);
				this.dsoaEvent = eventType.createEvent(metadata, data);
			} else {
				this.eventTypeName = dsoaEvent.getEventType().getName();
			}

			this.notifyEvent();
		}

		private void notifyEvent() {

			String eventSource = (String) dsoaEvent.getMetadataProperty(Constants.EVENT_SOURCE)
					.getValue();

			// topic based on context
			String topic = String.format("%s/%s", eventTypeName, eventSource);
			topic = topic.replaceAll("\\" + Constants.TOKEN, "/");

			Map<String, Object> eventTable = new HashMap<String, Object>();
			eventTable.put(eventTypeName, dsoaEvent.toMap());
			eventTable.put(Constants.REMOTE, dsoaEvent.isRemote());
			//TODO: add event data and metadata (permitir filtros sobre os campos dos eventos diretamente do event admin) 
			eventAdmin.postEvent(new org.osgi.service.event.Event(topic, eventTable));
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// //////////////internal class to listen arrivals from new adapters
	// ////////////////////////////////////////////////////////////////////////////////////////
	class AdapterCustomizer implements ServiceTrackerCustomizer {
		@Override
		public void removedService(ServiceReference reference, Object service) {
			EventAdapter adapter = (EventAdapter) service;
			eventAdapterCatalog.removeAdapter(adapter.getId());
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {

			EventAdapter adapter = (EventAdapter) service;
			eventAdapterCatalog.removeAdapter(adapter.getId());
			eventAdapterCatalog.addAdapter(adapter);
		}

		@Override
		public Object addingService(ServiceReference reference) {
			EventAdapter adapter = (EventAdapter) context.getService(reference);
			EventAdapter registerdAdapter = eventAdapterCatalog.addAdapter(adapter);

			//XXX: TEMP
			Collection<EventType> types = eventTypeCatalog.getAll();
			for (EventType eventType : types) {
				if (!eventType.isPrimitive()) {
					Map<String, Object> config = new HashMap<String, Object>();
					config.put(Constants.ADAPTER_ID, registerdAdapter.getId());
					exportEvents(eventType, config);
					importEvents(eventType, config);
				}
			}
			//XXX: TEMP
			
			return registerdAdapter;
		}
	}
}
