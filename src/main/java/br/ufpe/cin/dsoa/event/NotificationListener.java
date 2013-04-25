package br.ufpe.cin.dsoa.event;

import java.util.Map;

public interface NotificationListener  {
	
	public void receive(Map result, Object userObject,String statementName);
	
	public void receive(Object result, String statementName);

}
