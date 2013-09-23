package br.ufpe.cin.dsoa.platform.event;

import java.util.Collection;

import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeAlreadyCatalogedException;

public interface EventTypeCatalog {

	public EventType get(String typeName);

	public Collection<EventType> getAll();

	public EventType add(EventType eventType)
			throws EventTypeAlreadyCatalogedException;

	public EventType remove(String typeName);

	public boolean contains(String typeName);
}
