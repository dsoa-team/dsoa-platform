package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.event.legacy.EventType;

public class EventCatalogImpl {
	private static Map<String, EventType> eventTypes = new HashMap<String, EventType>();
	
	public static void put(String name, EventType eventType) {
		eventTypes.put(name, eventType);
	}
	
	public static EventType get(String name) {
		return eventTypes.get(name);
	}
}
