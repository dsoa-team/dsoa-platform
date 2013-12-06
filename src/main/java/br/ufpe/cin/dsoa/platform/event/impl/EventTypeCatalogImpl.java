package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;

public class EventTypeCatalogImpl implements EventTypeCatalog {

	private Map<String, EventType> eventTypes = new HashMap<String, EventType>();

	public EventType get(String typeName) {
		EventType eventType = this.eventTypes.get(typeName);
		return eventType;
	}

	public Collection<EventType> getAll() {
		return this.eventTypes.values();
	}

	public synchronized EventType add(EventType eventType)
			throws EventTypeAlreadyCatalogedException {

		String key = eventType.getName();
		if (this.eventTypes.containsKey(key)) {
			throw new EventTypeAlreadyCatalogedException(eventType);
		}

		EventType registeredEventType = this.eventTypes.put(key, eventType);

		return registeredEventType;
	}

	public synchronized EventType remove(String typeName) {
		EventType removedEventType = this.eventTypes.remove(typeName);

		return removedEventType;
	}

	public boolean contains(String typeName) {
		boolean contains = this.eventTypes.containsKey(typeName);

		return contains;
	}

}
