package br.ufpe.cin.dsoa.platform.event;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;


public interface EventProcessingService {

	public void registerEventType(EventType eventType);
	
	public void unregisterEventType(EventType eventType);
	
	public void publish(Event event);
	
	public void subscribe(EventConsumer consumer, Subscription subscription);
	
	public void unsubscribe(EventConsumer consumer, Subscription subscription);
	
	public void registerAgent(EventProcessingAgent agent);
	
	public void unRegisterAgent(String agentId);
	
}
