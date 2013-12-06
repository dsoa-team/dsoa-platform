package br.ufpe.cin.dsoa.api.event;

import java.util.Map;

public interface EventAdapter {
	
	public String getId();

	public void exportEvent(Event event, Map<String, Object> configuration);
	
	public void importEvent(EventConsumer consumer, Subscription subscription); //send to edservice queue servuce
	
}
