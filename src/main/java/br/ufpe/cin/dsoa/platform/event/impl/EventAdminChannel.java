package br.ufpe.cin.dsoa.platform.event.impl;

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

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventChannel;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.OutputTerminal;
import br.ufpe.cin.dsoa.util.Constants;

public class EventAdminChannel implements EventChannel, EventHandler  {

	private String topic;
	private ExecutorService executorService;
	private ServiceTracker tracker;
	private OutputTerminal output;
	private BundleContext ctx;

	public EventAdminChannel(BundleContext ctx, EventType eventType, OutputTerminal output) {
		
		String eventTopic = String.format("%s%s%s", Constants.REQUIRES_TAG_NAMESPACE, Constants.TOKEN, eventType.getName());
		this.topic = eventTopic.replaceAll("\\.", "/");
		this.output = output;
		this.executorService = Executors.newCachedThreadPool();
		/*this.tracker = new ServiceTracker(ctx,
				EventAdmin.class.getName(), null );
				tracker.open();*/
		this.ctx = ctx;
				
		Hashtable props = new Hashtable();
		props.put(EventConstants.EVENT_TOPIC, new String[] {topic});
		ctx.registerService(EventHandler.class.getName(), this , props);
	}
	
	@Override
	public void pushEvent(Event event) {
		
		ServiceReference ref  = ctx.getServiceReference(EventAdmin.class.getName());
		EventAdmin ea = (EventAdmin) ctx.getService(ref);
		/*EventAdmin ea = 
			(EventAdmin) tracker.getService();*/
			if ( ea != null ) {
				Map<String, Event> eventTable = new HashMap<String, Event>();
				eventTable.put("event", event);
				ea.postEvent(new org.osgi.service.event.Event(this.topic, eventTable));
			}
	}
	


	@Override
	public void sendEvent(Event event) {
		output.handle(event);
	}
	

	@Override
	public void handleEvent(org.osgi.service.event.Event event) {
		Worker worker = new Worker(event);
		executorService.execute(worker);
	}
	
	private class Worker implements Runnable{

		private Event event;
		
		public Worker(org.osgi.service.event.Event event) {
			this.event = (Event)event.getProperty("event");
		}
		
		@Override
		public void run() {
			output.handle(event);
		}
		
	}
}
