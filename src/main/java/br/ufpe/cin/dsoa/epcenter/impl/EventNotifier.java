package br.ufpe.cin.dsoa.epcenter.impl;

import br.ufpe.cin.dsoa.epcenter.NotificationListener;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

public class EventNotifier implements StatementAwareUpdateListener{

	private NotificationListener consumer;

	public EventNotifier(NotificationListener consumer) {
		this.consumer = consumer;
	}

	public void update(EventBean[] newEvents, EventBean[] oldEvents,
			EPStatement statement, EPServiceProvider epServiceProvider) {
		
		for (int i = 0; i < newEvents.length; i++) {
			Object result = newEvents[i].getUnderlying();
			this.consumer.receive(result, statement.getName());
		}
	}
}
