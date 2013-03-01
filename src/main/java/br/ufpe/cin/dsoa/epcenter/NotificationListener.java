package br.ufpe.cin.dsoa.epcenter;

import java.util.Map;

public interface NotificationListener {
	
	@SuppressWarnings("rawtypes")
	public void receive(Map result, Object userObject,String statementName);
	
	public void receive(Object result, String statementName);

}
