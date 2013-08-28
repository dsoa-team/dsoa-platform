package br.ufpe.cin.dsoa.platform.event;

import java.util.Collection;

import br.ufpe.cin.dsoa.event.agent.AgentAlreadyCatalogedException;
import br.ufpe.cin.dsoa.event.agent.EventProcessingAgent;

public interface AgentCatalog {
	public EventProcessingAgent getAgent(String id);

	public Collection<EventProcessingAgent> getAgents();

	public EventProcessingAgent addAgent(EventProcessingAgent eventProcessingAgent) throws AgentAlreadyCatalogedException;

	public EventProcessingAgent removeAgent(String id);
}
