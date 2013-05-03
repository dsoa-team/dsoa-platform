package br.ufpe.cin.dsoa.event;


import java.util.Map;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.espertech.esper.util.UuidGenerator;

public class EventNotifier implements StatementAwareUpdateListener{

	private NotificationListener consumer;

	public EventNotifier(NotificationListener consumer) {
		this.consumer = consumer;
	}

	public void update(EventBean[] newEvents, EventBean[] oldEvents,
			EPStatement statement, EPServiceProvider epServiceProvider) {
		
		for (int i = 0; i < newEvents.length; i++) {
			Object result = newEvents[i].getUnderlying();
			if (result instanceof Map<?, ?>) {
				((Map) result).put("id", UuidGenerator.generate());
				((Map) result).put("timestamp", System.currentTimeMillis());
			}
			this.consumer.receive(result, statement.getName());
		}
	}
}
