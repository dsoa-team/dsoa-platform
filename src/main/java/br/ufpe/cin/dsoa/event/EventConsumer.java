package br.ufpe.cin.dsoa.event;

import br.ufpe.cin.dsoa.event.subscription.EventFilter;

public interface EventConsumer {
	
	public String getId();
	public EventFilter getFilteringExpression();
	public void subscribe(Subscription subscription);
}

