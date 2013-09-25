package br.ufpe.cin.dsoa.platform.monitor;

import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.Subscription;

public class MonitoringRegistration {

	private EventConsumer consumer;
	private Subscription subscription;

	public MonitoringRegistration(EventConsumer consumer, Subscription subscription) {
		this.consumer = consumer;
		this.subscription = subscription;
	}

	public EventConsumer getConsumer() {
		return consumer;
	}

	public Subscription getSubscription() {
		return subscription;
	}
	

}
