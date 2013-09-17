package br.ufpe.cin.dsoa.api.event;


public class Subscription {

	private EventType eventType;
	
	private EventFilter filter;

	private String id;

	public Subscription(String id, EventType eventType, EventFilter filter) {
		if (eventType == null) {
			throw new IllegalArgumentException();
		}
		this.id = id;
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
