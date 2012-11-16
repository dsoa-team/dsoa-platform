package br.ufpe.cin.dsoa.agent;

import br.ufpe.cin.dsoa.event.InvocationEventOld;

public interface MetricAggregator {
	
	public Object handle(InvocationEventOld invocation);
}
