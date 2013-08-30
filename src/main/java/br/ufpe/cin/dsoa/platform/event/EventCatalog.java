package br.ufpe.cin.dsoa.platform.event;

import br.ufpe.cin.dsoa.event.legacy.EventType;

public interface EventCatalog {

	public boolean addEvent(EventType event);
}
