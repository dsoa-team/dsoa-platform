package br.ufpe.cin.dsoa.agent;

import br.ufpe.cin.dsoa.event.InvocationEvent;

public interface MetricComputer {

	public Object transform(InvocationEvent invocation);
	
}
