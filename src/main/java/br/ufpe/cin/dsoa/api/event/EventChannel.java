package br.ufpe.cin.dsoa.api.event;

public interface EventChannel {

	public void pushEvent(Event event);
	
	public void sendEvent(Event event);
	
}
