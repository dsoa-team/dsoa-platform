package br.ufpe.cin.dsoa.handlers;

import br.ufpe.cin.dsoa.event.InvocationEvent;

public interface MetricHandler {

	public Object handle(InvocationEvent invocation);
}
