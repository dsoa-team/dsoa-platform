package br.ufpe.cin.dsoa.epcenter;

import java.util.List;
import java.util.Map;

public interface EventProcessingCenter {

	public void defineEvent(Class<?> event);
	
	public void defineEvent(String eventName, Map<String, Object> eventProperties);

	public void publishEvent(Object event);

	public void publishEvent(Map<?,?> event, String eventName);

	public void defineStatement(String name, String statement, List<String> userObject);
	
	public void defineStatement(String name, String statement);
	
	public void subscribe(String statementName, NotificationListener eventConsumer);
	
	public void unsubscribe(String statementName, NotificationListener eventConsumer);

	public void undefineEvents();
	
	public void undefineEvent(String eventName);
	
	public void undefineEvent(Class eventClass);

	public void defineContext(String ctxStatement);

}
