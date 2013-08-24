package br.ufpe.cin.dsoa.event;

public interface EventConsumer {
	
	public String getId();
	public EventFilter getFilteringExpression();
	public void subscribe(Subscription subscription);
}

