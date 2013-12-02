package br.ufpe.cin.dsoa.api.event;

public interface EventAdapter {
	
	public String getId();

	public void handleEvent(Event event);
	
	public void postEvent(String eventTypeName, Object event);
}
