package br.ufpe.cin.dsoa.event.agent.excetpion;

import br.ufpe.cin.dsoa.event.agent.meta.EventProcessingAgent;


public class AgentAlreadyCatalogedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1934742999681727321L;
	private static final String ERROR_MSG = "EventProcessingAgent already cataloged: %s";
	
	public AgentAlreadyCatalogedException(EventProcessingAgent eventProcessingAgent) {
		super(String.format(ERROR_MSG, eventProcessingAgent.getId()));
	}

}
