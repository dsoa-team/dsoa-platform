package br.ufpe.cin.dsoa.platform.event;

import java.util.Collection;

import br.ufpe.cin.dsoa.api.event.EventAdapter;

public interface EventAdapterCatalog {
	
	public EventAdapter get(String adapterId);

	public Collection<EventAdapter> getAdapters();

	public EventAdapter addAdapter(EventAdapter eventAdapter);

	public EventAdapter removeAdapter(String adapterId);

}
