package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.agent.AgentAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingMapping;
import br.ufpe.cin.dsoa.platform.event.AgentCatalog;

/**
 * This component implements a catalog that register the agents that are known
 * by the platform and that can be used to build Event Processing Networks.
 * 
 * 
 * @author fabions
 * 
 */
public class AgentCatalogImpl implements AgentCatalog {
	private Map<String, EventProcessingAgent> agentMap = new HashMap<String, EventProcessingAgent>();

	public EventProcessingAgent getAgent(String id) {
		return this.agentMap.get(id);
	}

	public Collection<EventProcessingAgent> getAgents() {
		return this.agentMap.values();
	}

	public synchronized EventProcessingAgent addAgent(EventProcessingAgent eventProcessingAgent)
			throws AgentAlreadyCatalogedException {
		if (this.agentMap.containsKey(eventProcessingAgent.getId())) {
			throw new AgentAlreadyCatalogedException(eventProcessingAgent);
		}

		return this.agentMap.put(eventProcessingAgent.getId(), eventProcessingAgent);
	}

	public EventProcessingAgent removeAgent(String id) {
		return this.agentMap.remove(id);
	}

	@Override
	public EventProcessingAgent getAgent(EventType outputEventType) {

		Iterator<EventProcessingAgent> agents = this.agentMap.values().iterator();
		while (agents.hasNext()) {
			EventProcessingAgent eventProcessingAgent = agents.next();
			if (eventProcessingAgent.getProcessing() instanceof ProcessingMapping) {
				ProcessingMapping mapping = (ProcessingMapping) eventProcessingAgent
						.getProcessing();
				if (mapping.getOutputEvent().getType().equals(outputEventType.getName())) {
					return eventProcessingAgent;
				}
			}
		}

		return null;
	}

}
