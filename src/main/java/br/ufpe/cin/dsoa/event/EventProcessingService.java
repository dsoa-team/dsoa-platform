package br.ufpe.cin.dsoa.event;

import br.ufpe.cin.dsoa.agent.EventProcessingAgent;


public interface EventProcessingService {

	public void subscribe(String consumerId, Subscription subscription);
	
	public void publish(Event event);
	
	public boolean registerConsumer(EventConsumer consumer);
	
	public boolean registerAgent(EventProcessingAgent agent);
	
	public boolean unRegisterConsumer(String consumerId);
	
	public boolean unRegisterAgent(String agentId);
	
}
