package br.ufpe.cin.dsoa.epcenter;

import java.util.Map;

public interface EventConsumer {
	
	@SuppressWarnings("rawtypes")
	public void receive(Map result, Object userObject,String statementName);

}
