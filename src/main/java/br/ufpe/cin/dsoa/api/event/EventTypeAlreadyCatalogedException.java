package br.ufpe.cin.dsoa.api.event;

public class EventTypeAlreadyCatalogedException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1934742999681727321L;
	
	private static final String ERROR_MSG = "EventType already cataloged: %s";

	public EventTypeAlreadyCatalogedException(EventType eventType) {
		super(String.format(ERROR_MSG, eventType.getName()));
	}

}
