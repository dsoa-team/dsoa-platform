package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Map;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.Subscription;

public class EventSubscriber {

	private EventConsumer consumer;
	protected Subscription subcription;

	public EventSubscriber(EventConsumer consumer, Subscription subscription) {
		this.consumer = consumer;
		this.subcription = subscription;
	}
	
	public void update(Map esperEvent) {
		
	}

}
