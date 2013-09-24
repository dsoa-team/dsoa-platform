package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventChannel;
import br.ufpe.cin.dsoa.api.event.OutputTerminal;

public class EventAdminChannel implements EventChannel, EventHandler  {

	private BundleContext ctx;
	private String topic;
	private ExecutorService executorService;
	private ServiceTracker tracker;
	private OutputTerminal output;

	public EventAdminChannel(BundleContext ctx, String topic, OutputTerminal output) {
		this.ctx = ctx;
		this.topic = topic;
		this.output = output;
		this.executorService = Executors.newCachedThreadPool();
		this.tracker = new ServiceTracker(ctx,
				EventAdmin.class.getName(), null );
				tracker.open();
				
		Hashtable props = new Hashtable();
		props.put(EventConstants.EVENT_TOPIC, new String[] {topic});
		ctx.registerService(EventHandler.class.getName(), this , props);
	}
	
	@Override
	public void pushEvent(Event event) {
		EventAdmin ea = 
			(EventAdmin) tracker.getService();
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
