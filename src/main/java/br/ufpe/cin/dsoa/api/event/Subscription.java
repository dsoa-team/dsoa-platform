package br.ufpe.cin.dsoa.api.event;

import java.util.UUID;


public class Subscription {

	private EventType eventType;
	
	private EventFilter filter;

	private String id;

	public Subscription(EventType eventType, EventFilter filter) {
		if (eventType == null) {
			throw new IllegalArgumentException();
		}
		this.id = String.format("%s-%s", eventType.getName(), UUID.randomUUID().toString());
		this.eventType = eventType;
		this.filter = filter;
	}

	public EventType getEventType() {
		return this.eventType;
	}
	
	public EventFilter getFilter() {
		return this.filter;
	}

	public String getId() {
		return id;
	}
	
	
}
