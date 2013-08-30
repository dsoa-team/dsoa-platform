package br.ufpe.cin.dsoa.platform.event;

import br.ufpe.cin.dsoa.event.meta.EventType;

public interface EventCatalog {

	public boolean addEvent(EventType event);
}
