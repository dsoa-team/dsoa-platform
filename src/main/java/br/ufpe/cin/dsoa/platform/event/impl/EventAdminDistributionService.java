package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventAdapter;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.platform.event.EventAdapterCatalog;
import br.ufpe.cin.dsoa.platform.event.EventDistribuitionService;
import br.ufpe.cin.dsoa.platform.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.util.MetadataEnricher;

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
		this.executorService = Executors.newCachedThreadPool();

		this.adapterTracker = new ServiceTracker(this.context,
				EventAdapter.class.getName(), new AdapterCustomizer());
		this.adapterTracker.open();
	}

	public void stop() {
		this.executorService.shutdown();
		this.adapterTracker.close();
	}

	@Override
	public void postEvent(String eventTypeName, Map<String, Object> metadata,
			Map<String, Object> data) {
		NotificationWorker worker = new NotificationWorker(eventTypeName,
				metadata, data);
		executorService.execute(worker);
	}
	
	@Override
	public void postEvent(String adapterName, Object event) {

	}

	class NotificationWorker implements Runnable {

		private String eventTypeName;
		private Map<String, Object> metadata;
		private Map<String, Object> data;

		public NotificationWorker(String eventTypeName,
				Map<String, Object> metadata, Map<String, Object> data) {
			super();
			this.eventTypeName = eventTypeName;
			this.metadata = metadata;
			this.data = data;
		}

		@Override
		public void run() {
			this.notifyInvocation();
		}

		private void notifyInvocation() {
			EventType eventType = eventTypeCatalog.get(eventTypeName);
			addGeneratedMetadataProperties(eventType);

			Event dsoaEvent = eventType.createEvent(metadata, data);
			String topic = eventTypeName;

			Map<String, Object> eventTable = new HashMap<String, Object>();
			eventTable.put(topic, dsoaEvent.toMap());
			eventAdmin.postEvent(new org.osgi.service.event.Event(topic,
					eventTable));
		}

		private void addGeneratedMetadataProperties(EventType eventType) {
			for (PropertyType propertyType : eventType.getMetadataList()) {
				if (propertyType.isGenerated()) {
					String key = propertyType.getName();
					Object value = MetadataEnricher.generate(key);
					metadata.put(key, value);
				}
			}
		}
	}

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
			
			return registerdAdapter;
		}
	}

}
