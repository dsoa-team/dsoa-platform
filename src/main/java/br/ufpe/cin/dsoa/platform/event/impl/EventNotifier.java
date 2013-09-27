package br.ufpe.cin.dsoa.platform.event.impl;


import java.util.Map;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.attribute.AttributeChangeListener;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.espertech.esper.util.UuidGenerator;

public class EventNotifier implements StatementAwareUpdateListener{

	private AttributeChangeListener consumer;

	public EventNotifier(AttributeChangeListener consumer) {
		this.consumer = consumer;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void update(EventBean[] newEvents, EventBean[] oldEvents,
			EPStatement statement, EPServiceProvider epServiceProvider) {
		
		for (int i = 0; i < newEvents.length; i++) {
			Object result = newEvents[i].getUnderlying();
			if (result instanceof Map<?, ?>) {
				((Map) result).put("id", UuidGenerator.generate());
				((Map) result).put("timestamp", System.currentTimeMillis());
				this.consumer.update(this.buildAttributeValue(newEvents[i]));
			}
		}
	}

	private AttributeValue buildAttributeValue(EventBean eventBean) {
		// TODO Auto-generated method stub
		return null;
	}
}
