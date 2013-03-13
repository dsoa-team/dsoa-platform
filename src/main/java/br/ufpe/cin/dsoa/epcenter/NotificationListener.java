package br.ufpe.cin.dsoa.epcenter;

import java.util.Map;

public interface NotificationListener  {
	
	public String getServiceId();
	
	public String getOperationName();
	
	@SuppressWarnings("rawtypes")
	public void receive(Map result, Object userObject,String statementName);
	
	public void receive(Object result, String statementName);

}
