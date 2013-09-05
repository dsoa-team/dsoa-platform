package br.ufpe.cin.dsoa.api.event;


public interface EventProducer {
	
	public String getId();
	
	public EventType[] getEventTypes();
	
	// public EventChannel getEventChannel();
	
	// public EventChannel setEventChannel(EventChannel channel);
	
}
