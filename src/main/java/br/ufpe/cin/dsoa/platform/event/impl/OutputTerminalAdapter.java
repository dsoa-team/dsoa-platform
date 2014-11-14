package br.ufpe.cin.dsoa.platform.event.impl;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventProcessingService;
import br.ufpe.cin.dsoa.api.event.OutputTerminal;

public class OutputTerminalAdapter implements OutputTerminal {

	private EventProcessingService epService;

	public OutputTerminalAdapter(EventProcessingService epService) {
		this.epService = epService;
	}
	
	@Override
	public void handle(Event event) {
		this.epService.publish(event);
	}

}
