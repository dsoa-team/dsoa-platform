package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.Subscription;

public class EventSubscriber {

	private EventConsumer consumer;
	protected Subscription subcription;

	public EventSubscriber(EventConsumer consumer, Subscription subscription) {
		this.consumer = consumer;
		this.subcription = subscription;
	}
	
	public void update(Map<String, Object> esperEvent) {
		EventType eventType = subcription.getEventType();
		Event dsoaEvent = eventType.createEvent(esperEvent);
		consumer.handleEvent(dsoaEvent);
	}

}
