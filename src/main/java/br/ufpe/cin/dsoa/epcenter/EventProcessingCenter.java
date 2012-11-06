package br.ufpe.cin.dsoa.epcenter;

import java.util.List;
import java.util.Map;

public interface EventProcessingCenter {

	public void defineEvent(Class<?> event);

	public void publishEvent(Object event);

	public void publishEvent(Map<?,?> event, String eventName);

	public void defineStatement(String name, String statement, List<String> userObject);
	
	public void defineStatement(String name, String statement);

}
