package br.ufpe.cin.dsoa.agent;

import br.ufpe.cin.dsoa.event.InvocationEvent;

public class ResponseTimeTransformer implements Derivator<InvocationEvent>{

	public Object derive(InvocationEvent event) {
		return event.getResponseTimestamp() - event.getRequestTimestamp();
	}

}
