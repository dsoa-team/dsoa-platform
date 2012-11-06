package br.ufpe.cin.dsoa.epcenter.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.agent.MetricComputer;
import br.ufpe.cin.dsoa.agent.store.StorageAgentService;
import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.event.InvocationEvent;

public class EventDispatcher implements EventHandler {

	private int poolSize = 30;
	private int maxPoolSize = 50;
	private long keepAliveTime = 10;
	private List<StorageAgentService> registryList;
	private List<EventProcessingCenter> epCenterList;

	final ArrayBlockingQueue<Runnable> queue;

	private ThreadPoolExecutor threadPool;

	public EventDispatcher(final BundleContext ctx) {
		registryList = new ArrayList<StorageAgentService>();
		epCenterList = new ArrayList<EventProcessingCenter>();

		queue = new ArrayBlockingQueue<Runnable>(100);
		threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize,
				keepAliveTime, TimeUnit.SECONDS, queue);

		new ServiceTracker(ctx, StorageAgentService.class.getName(), new ServiceTrackerCustomizer() {
			
			public Object addingService(ServiceReference reference) {
				StorageAgentService service = (StorageAgentService) ctx.getService(reference);
				registryList.add(service);

				return service;
			}

			public void modifiedService(ServiceReference reference, Object service) {
			}

			public void removedService(ServiceReference reference, Object service) {
				registryList.remove(service);
			}
		}).open();

		new ServiceTracker(ctx, EventProcessingCenter.class.getName(),
				new ServiceTrackerCustomizer() {

					public void removedService(ServiceReference reference, Object service) {
						epCenterList.remove(service);
					}

					public void modifiedService(ServiceReference reference, Object service) {

					}

					public Object addingService(ServiceReference reference) {
						EventProcessingCenter service = (EventProcessingCenter) ctx.getService(reference);
						epCenterList.add(service);
						return service;
					}
				}).open();
	}

	public void handleEvent(Event event) {

		final InvocationEvent invocationEvent = (InvocationEvent) event
				.getProperty(event.getTopic());

		if (!registryList.isEmpty() && !epCenterList.isEmpty()) {
			
			final StorageAgentService registry = registryList.get(0);
			final EventProcessingCenter epCenter = epCenterList.get(0);
			
			Runnable publisher = new Runnable() {
				public void run() {
					List<String> attrs = invocationEvent.getMonitoredAttributes();
					
					for (String attr : attrs) {
						MetricComputer agent = registry.getMetricAgent(attr);
						
						if (null != agent) {
							Object receivedEvent = agent.transform(invocationEvent);
							epCenter.publishEvent(receivedEvent);
						}
					}
				}
			};

			threadPool.execute(publisher);
		}
	}
}
