package br.ufpe.cin.dsoa.event.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.event.EventNotifier;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.event.InvocationEvent;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.event.Statement;
import br.ufpe.cin.dsoa.metric.MetricMonitor;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPPreparedStatement;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class EventProcessingServiceImpl implements EventProcessingService {

	private EPServiceProvider epServiceProvider;
	private final List<String> eventNames;
	private Map<String, EPPreparedStatement> mapPreparedStmts;
	private Map<String, EPStatement> mapStmts;

	public EventProcessingServiceImpl(BundleContext context) {
		this();
	}
	
	public EventProcessingServiceImpl() {
		this.eventNames = new ArrayList<String>();
		this.mapPreparedStmts = new HashMap<String, EPPreparedStatement>();
		this.mapStmts = new HashMap<String, EPStatement>();
	}
	
	public void start() {
		this.epServiceProvider = EPServiceProviderManager.getProvider(
				"EngineInstance", new Configuration());
		this.configureEvents();
	}

	public void stop() {
		this.epServiceProvider.destroy();
	}
	
	private void configureEvents() {
		this.defineEvent(InvocationEvent.class);
	}
	
	public void publishEvent(Object event) {
		this.epServiceProvider.getEPRuntime().sendEvent(event);
	}

	@SuppressWarnings("rawtypes")
	public void publishEvent(Map event, String eventName) {
		this.eventNames.add(eventName);
		this.epServiceProvider.getEPRuntime().sendEvent(event, eventName);
	}

	@SuppressWarnings("rawtypes")
	public void defineEvent(Class eventClass) {
		this.eventNames.add(eventClass.getSimpleName());
		this.epServiceProvider.getEPAdministrator().getConfiguration().addEventType(eventClass);
	}
	
	public void defineStatement(String name, String statement) {
		EPPreparedStatement prepared = epServiceProvider.getEPAdministrator().prepareEPL(statement);
		this.mapPreparedStmts.put(name, prepared);
	}
	
	public void destroyStatement(String name) {
		epServiceProvider.getEPAdministrator().getStatement(name).destroy();
	}
	
	public void defineStatement(String name, String statement, List<String> userObject) {
		this.epServiceProvider.getEPAdministrator().createEPL(statement, name, userObject);
	}

	public void defineEvent(String eventName, Map<String, Object> eventProperties) {
		this.epServiceProvider.getEPAdministrator().getConfiguration().
			addEventType(eventName, eventProperties);
		
	}
	
	public void subscribe(String statementName, List<Object> parameters, final NotificationListener eventConsumer) {
		EPPreparedStatement preparedStmt = this.mapPreparedStmts.get(statementName);
		int index = 0;
		for (Object parameter : parameters) {
			preparedStmt.setObject(++index, parameter);
		}
		
		StringBuffer stmtName = new StringBuffer(statementName).append("[");
		boolean firstParam = true;
		for (Object parameter : parameters) {
			if(!firstParam) {
				stmtName.append(",");
			} else {
				firstParam = false;
			}
			stmtName.append(parameter);
		}
		stmtName.append("]");
		EPStatement statement = this.epServiceProvider.getEPAdministrator().create(preparedStmt, stmtName.toString());
		this.mapStmts.put(stmtName.toString(), statement);
		statement.addListener(new EventNotifier(eventConsumer));
	}
	
	public void unsubscribe(String statementName, NotificationListener eventConsumer) {
		/*EventNotifier notifier = this.notifierMap.get(statementName);
		
		if(notifier != null) {
			notifier.removeEventConsumer(eventConsumer);
			if(!notifier.hasEventConsumers()) {
				epServiceProvider.getEPAdministrator().getStatement(statementName).removeListener(notifier);
				this.notifierMap.remove(statementName);
			}
		}*/
	}

	public void undefineEvents() {
		for (String eventName : this.eventNames) {
			this.undefineEvent(eventName);
		}
	}
	
	public void undefineEvent(Class eventClass) {
		this.undefineEvent(eventClass.getSimpleName());
	}
	
	public void undefineEvent(String eventName) {
		this.epServiceProvider.getEPAdministrator().getConfiguration().removeEventType(eventName, true);
	}
	
	public void defineContext(String ctxStatement) {
		this.epServiceProvider.getEPAdministrator().createEPL(ctxStatement);
	}

	@Override
	public void defineStatement(Statement stmt) {
		this.epServiceProvider.getEPAdministrator().createEPL(stmt.getQuery(), stmt.getName());		
	}
	
	public void subscribe(String statementName, final NotificationListener eventConsumer) {
		EPStatement statement = this.epServiceProvider.getEPAdministrator().getStatement(statementName);
		statement.addListener(new EventNotifier(eventConsumer));
	}
}
