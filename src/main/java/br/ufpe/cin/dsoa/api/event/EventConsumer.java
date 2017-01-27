package br.ufpe.cin.dsoa.api.event;


public interface EventConsumer {
	
	public String getComponentInstanceName();
	
	public void handleEvent(Event event);
	
}

