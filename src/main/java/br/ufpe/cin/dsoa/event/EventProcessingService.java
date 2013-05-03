package br.ufpe.cin.dsoa.event;

import java.util.List;
import java.util.Map;

public interface EventProcessingService {

	public void defineEvent(Class<?> event);
	
	public void defineEvent(String eventName, Map<String, Object> eventProperties);

	public void publishEvent(Object event);

	public void publishEvent(Map<?,?> event, String eventName);

	public void defineStatement(String name, String statement, List<String> userObject);
	
	public void defineStatement(String name, String statement);
	
	public void destroyStatement(String name);
	
	public void subscribe(String statementName, List<Object> parameters, NotificationListener eventConsumer);
	
	public void unsubscribe(String statementName, NotificationListener eventConsumer);

	public void undefineEvents();
	
	public void undefineEvent(String eventName);
	
	public void undefineEvent(Class eventClass);

	public void defineStatement(Statement stmt1);

	public void subscribe(String statementName, NotificationListener eventConsumer);
}
