package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.api.event.agent.AgentAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.platform.event.AgentCatalog;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;

/**
 * This component implements a catalog that register the agents that are known by the platform and that can be used
 * to build Event Processing Networks. 
 * 
 * 
 * @author fabions
 *
 */
public class AgentCatalogImpl implements AgentCatalog {
	private Map<String,EventProcessingAgent> agentMap = new HashMap<String,EventProcessingAgent>();
	private EventProcessingService epService;
	
	public EventProcessingAgent getAgent(String id) {
		return this.agentMap.get(id);
	}

	public Collection<EventProcessingAgent> getAgents() {
		return this.agentMap.values();
	}

	public synchronized EventProcessingAgent addAgent(EventProcessingAgent eventProcessingAgent) throws AgentAlreadyCatalogedException {
		if (this.agentMap.containsKey(eventProcessingAgent.getId())) {
			throw new AgentAlreadyCatalogedException(eventProcessingAgent);
		}
		
		this.epService.registerAgent(eventProcessingAgent);
		return this.agentMap.put(eventProcessingAgent.getId(), eventProcessingAgent);
	}

	public EventProcessingAgent removeAgent(String id) {
		return this.agentMap.remove(id);
	}

}
