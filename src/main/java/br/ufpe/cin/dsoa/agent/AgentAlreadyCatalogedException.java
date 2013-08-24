package br.ufpe.cin.dsoa.agent;


public class AgentAlreadyCatalogedException extends Exception {
	
	private static final String ERROR_MSG = "EventProcessingAgent already cataloged: %s";
	
	public AgentAlreadyCatalogedException(EventProcessingAgent eventProcessingAgent) {
		super(String.format(ERROR_MSG, eventProcessingAgent.getId()));
	}

}
