package br.ufpe.cin.dsoa.api.event;

public interface EventAdapter {
	
	public String getId();

	public void exportEvent(Event event);
	
	public void importEvent(Subscription subscription); //send to edservice queue servuce
	
}
