package br.ufpe.cin.dsoa.agent;

import br.ufpe.cin.dsoa.event.InvocationEventOld;

public class ResponseTimeTransformer implements Derivator<InvocationEventOld>{

	public Object derive(InvocationEventOld event) {
		return event.getResponseTimestamp() - event.getRequestTimestamp();
	}

}
