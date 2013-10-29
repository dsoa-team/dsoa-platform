package br.ufpe.cin.dsoa.platform.event;

import java.util.Collection;

import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.agent.AgentAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;

public interface AgentCatalog {
	
	public EventProcessingAgent getAgent(EventType outputEventType);

	public Collection<EventProcessingAgent> getAgents();

	public EventProcessingAgent addAgent(EventProcessingAgent eventProcessingAgent) throws AgentAlreadyCatalogedException;

	public EventProcessingAgent removeAgent(String id);
}
