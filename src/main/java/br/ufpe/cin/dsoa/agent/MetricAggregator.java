package br.ufpe.cin.dsoa.agent;

import br.ufpe.cin.dsoa.event.InvocationEvent;

public interface MetricAggregator {
	
	public Object handle(InvocationEvent invocation);
}
