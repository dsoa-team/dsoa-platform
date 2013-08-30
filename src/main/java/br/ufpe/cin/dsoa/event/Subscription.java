package br.ufpe.cin.dsoa.event;

import br.ufpe.cin.dsoa.event.subscription.EventFilter;

public class Subscription {

	private String eventType;
	
	private EventConsumerSubscriber subscriber;
	
	private EventFilter filter;

	public Subscription(EventConsumerSubscriber subscriber, EventFilter filter) {
		this.subscriber = subscriber;
		this.filter = filter;
	}

	public EventConsumerSubscriber getSubscriber() {
		return subscriber;
	}

	public EventFilter getFilter() {
		return filter;
	}

}
