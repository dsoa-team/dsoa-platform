package br.ufpe.cin.dsoa.epcenter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.epcenter.EventConsumer;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.espertech.esper.event.map.MapEventBean;

public class EventNotifier implements StatementAwareUpdateListener{

	private List<EventConsumer> consumers;
	private EPServiceProvider epServiceProvider;

	public EventNotifier(EPServiceProvider epServiceProvider) {
		this.epServiceProvider = epServiceProvider;
		this.consumers = new ArrayList<EventConsumer>();
	}

	public void addEventConsumer(EventConsumer eventConsumer) {
		this.consumers.add(eventConsumer);
	}

	public void update(EventBean[] newEvents, EventBean[] oldEvents,
			EPStatement statement, EPServiceProvider epServiceProvider) {
		
		for(EventConsumer consumer : consumers) {
			for (int i = 0; i < newEvents.length; i++) {
				MapEventBean eventBean = (MapEventBean) newEvents[i];
				Map<String,Object> result = eventBean.getProperties();
				consumer.receive(result,statement.getText(),statement.getName());
			}
		}
		
		if (statement.getName().startsWith("VioXy")){
			@SuppressWarnings("unchecked")
			List<String> statementsNames = (List<String>) statement.getUserObject(); 
			for(String statementName: statementsNames){
				this.epServiceProvider.getEPAdministrator().getStatement(statementName).destroy();
			}
			//System.out.println("");
			//System.out.println("");
			//for(String nameSta : this.epServiceProvider.getEPAdministrator().getStatementNames()){
				//System.out.println(nameSta + " :: " + this.epServiceProvider.getEPAdministrator().getStatement(nameSta));
			//}
			//System.out.println("");
			//System.out.println("");
			
		}
		
	}

	public void removeEventConsumer(EventConsumer eventConsumer) {
		// TODO Auto-generated method stub
		
	}

	public boolean hasEventConsumers() {
		boolean result = false;
		if (null != consumers && consumers.size() != 0) {
			result = true;
		}
		return result;
	}

}
