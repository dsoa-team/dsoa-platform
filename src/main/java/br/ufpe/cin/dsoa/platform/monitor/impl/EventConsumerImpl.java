package br.ufpe.cin.dsoa.platform.monitor.impl;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.platform.monitor.MonitoredAttribute;

public class EventConsumerImpl implements EventConsumer {

	
	
	private MonitoredAttribute monitoredAttribute;

	public EventConsumerImpl(MonitoredAttribute monitoredAttribute) {
		this.monitoredAttribute = monitoredAttribute;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public void handleEvent(Event event) {

	}

}
