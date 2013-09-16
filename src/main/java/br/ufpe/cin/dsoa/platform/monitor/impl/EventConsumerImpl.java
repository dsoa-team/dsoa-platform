package br.ufpe.cin.dsoa.platform.monitor.impl;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredAttribute;

public class EventConsumerImpl implements EventConsumer {

	
	private String id;
	private MonitoredAttribute monitoredAttribute;
	private AttributeEventMapper mapper;

	public EventConsumerImpl(AttributeEventMapper mapper, MonitoredAttribute monitoredAttribute) {
		this.mapper = mapper;
		this.monitoredAttribute = monitoredAttribute;
		this.id = monitoredAttribute.getStatusVariableId();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void handleEvent(Event event) {
		AttributeValue attValue = mapper.convertToAttribute(event);
		monitoredAttribute.update(attValue);
	}

}
