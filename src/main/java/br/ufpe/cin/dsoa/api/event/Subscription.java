package br.ufpe.cin.dsoa.api.event;


public class Subscription {

	private String eventTypeName;
	
	private EventFilter filter;

	public Subscription(String eventTypeId, EventFilter filter) {
		if (eventTypeId == null) {
			throw new IllegalArgumentException();
		}
		this.eventTypeName = eventTypeId;
		this.filter = filter;
	}

	public String getEventTypeName() {
		return this.eventTypeName;
	}
	
	public EventFilter getFilter() {
		return this.filter;
	}
	
}
