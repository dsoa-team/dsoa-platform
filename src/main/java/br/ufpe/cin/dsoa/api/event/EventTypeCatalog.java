package br.ufpe.cin.dsoa.api.event;

import java.util.Collection;


public interface EventTypeCatalog {

	public EventType get(String typeName);

	public Collection<EventType> getAll();

	public EventType add(EventType eventType)
			throws EventTypeAlreadyCatalogedException;

	public EventType remove(String typeName);

	public boolean contains(String typeName);
}
