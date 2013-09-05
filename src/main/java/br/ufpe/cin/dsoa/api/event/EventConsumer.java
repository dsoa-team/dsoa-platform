package br.ufpe.cin.dsoa.api.event;


public interface EventConsumer {
	
	public String getId();
	
	public void handleEvent(Event event);
	
}

