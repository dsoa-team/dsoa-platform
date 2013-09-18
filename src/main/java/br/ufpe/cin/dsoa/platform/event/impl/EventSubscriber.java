package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.Subscription;

public class EventSubscriber {

	private EventConsumer consumer;
	protected Subscription subcription;

	public EventSubscriber(EventConsumer consumer, Subscription subscription) {
		this.consumer = consumer;
		this.subcription = subscription;
	}
	
	public void update(Map<String, Object> esperEvent) {
		EventType eventType = subcription.getEventType();
		
		Event dsoaEvent = null;
		Map<String, Object> metadata = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		

		// GABIA: NO CASO DE EVENTO PRIMITIVO, H� 2 MAPAS SEPARADOS, UM PARA METADADOS E OUTRO PARA DADOS
		String eventTypeName = (String)esperEvent.get("type");
		if (eventTypeName != null && eventTypeName.equalsIgnoreCase("InvocationEvent")) {
			data = (Map<String, Object>)esperEvent.get("data");
			metadata = (Map<String, Object>)esperEvent.get("metadata");
		} else {
			
			for (String key : ((Map<String, Object>) esperEvent).keySet()) {
				if (key.startsWith("data_")) {
					String newKey = key.replace("data_", "");
					data.put(newKey, esperEvent.get(key));
				} else if (key.startsWith("metadata_")) {
					String newKey = key.replace("metadata_", "");
					metadata.put(newKey, esperEvent.get(key));
				}
			}
		}
		dsoaEvent = eventType.createEvent(metadata, data);
		consumer.handleEvent(dsoaEvent);
	}

}
