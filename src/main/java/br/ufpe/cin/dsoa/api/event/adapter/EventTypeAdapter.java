package br.ufpe.cin.dsoa.api.event.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;

public class EventTypeAdapter extends XmlAdapter<String, EventType> {

	private EventProcessingService epService;

	public EventTypeAdapter(EventProcessingService epService) {
		this.epService = epService;
	}

	@Override
	public EventType unmarshal(String eventTypeName) throws Exception {
		EventType eventType = epService.getEventType(eventTypeName);
		return eventType;
	}

	@Override
	public String marshal(EventType eventType) throws Exception {
		return eventType.getName();
	}
}
