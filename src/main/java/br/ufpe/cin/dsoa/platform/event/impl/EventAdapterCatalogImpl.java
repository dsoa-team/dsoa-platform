package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.api.event.EventAdapter;
import br.ufpe.cin.dsoa.platform.event.EventAdapterCatalog;

public class EventAdapterCatalogImpl implements EventAdapterCatalog {

	private Map<String, EventAdapter> adapterMap = new HashMap<String, EventAdapter>();

	@Override
	public EventAdapter get(String adapterId) {
		EventAdapter adapter = this.adapterMap.get(adapterId);
		return adapter;
	}

	@Override
	public Collection<EventAdapter> getAdapters() {
		return this.adapterMap.values();
	}

	@Override
	public synchronized EventAdapter addAdapter(EventAdapter eventAdapter) {
		this.adapterMap.put(eventAdapter.getId(), eventAdapter);
		
		return eventAdapter;
	}

	@Override
	public synchronized EventAdapter removeAdapter(String adapterId) {
		
		EventAdapter removedAdapter = this.adapterMap.remove(adapterId);
		
		return removedAdapter;
	}

}
