package br.ufpe.cin.dsoa.agent;

import java.util.Collection;

public interface AgentCatalog {
	public EventProcessingAgent getAgent(String id);

	public Collection<EventProcessingAgent> getAgents();

	public EventProcessingAgent addAgent(EventProcessingAgent eventProcessingAgent) throws AgentAlreadyCatalogedException;

	public EventProcessingAgent removeAgent(String id);
}
