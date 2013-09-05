package br.ufpe.cin.dsoa.epcenter;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class TestEventStreams implements UpdateListener {

	String[] providers = { "provider-1", "provider-2", "provider-3" };
	String[] services = { "service-1", "service-2", "service-3", "service-4", "service-5" };
	String[] operations = { "foo", "bar" };
	int[] delays = { 1, 2, 3, 4, 5 };

	private EPServiceProvider epServiceProvider;
	
	private EPStatement stmt; 

	@Before
	public void setUp() {
		Configuration config = new Configuration();
		//config.addEventType(InvocationEvent.class);
		config.addPlugInSingleRowFunction("nextId", "br.ufpe.cin.dsoa.api.event.EventIdGenerator", "nextId");
		
		epServiceProvider = EPServiceProviderManager.getProvider("EngineInstance", config);
		String epl = null;
		//epl = "select nextId(*), current_timestamp(), avg(responseTimestamp) from InvocationEvent.win:length(2)";
		epl = "select service, operation from InvocationEvent";
		stmt = this.epServiceProvider.getEPAdministrator().createEPL(epl);
		stmt.addListener(this);

		/*
		 * EPStatement statement1 =
		 * this.epServiceProvider.getEPAdministrator().create(preparedStmt,
		 * stmtName.toString()); EPStatement statement2 =
		 * this.epServiceProvider.getEPAdministrator().createEPL(statement,
		 * name, userObject);
		 * this.epServiceProvider.getEPRuntime().sendEvent(event);
		 * this.epServiceProvider.getEPRuntime().sendEvent(event, eventName);
		 * this
		 * .epServiceProvider.getEPAdministrator().getConfiguration().addEventType
		 * (eventClass); EPPreparedStatement prepared =
		 * epServiceProvider.getEPAdministrator().prepareEPL(statement);
		 * EPStatement statement =
		 * this.epServiceProvider.getEPAdministrator().getStatement
		 * (statementName); statement.addListener(new
		 * EventNotifier(eventConsumer));
		 */
	}

	@Test
	public void sendEvents() {
		String provider;
		String service;
		String operation;
		boolean success;
		long requestTimestamp;
		long responseTimestamp;

		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			int n = i+1;
			System.out.println("==== i: " + i + " ====");
			provider = providers[random.nextInt(providers.length)];
			service = services[random.nextInt(services.length)];
			operation = operations[random.nextInt(operations.length)];
			success = true;
			requestTimestamp = i;
			responseTimestamp = i + n++;
			//InvocationEvent invocation = new InvocationEvent(provider, service, operation, success, requestTimestamp,
			//		responseTimestamp);
			//System.out.println(invocation);
			//this.epServiceProvider.getEPRuntime().sendEvent(invocation);
			/*System.out.println("----------------------------------------------------------------------------------------------------------");
			Iterator itr = stmt.iterator();
			while(itr.hasNext()) {
				System.out.println(((EventBean)itr.next()).getUnderlying());
			}
			System.out.println("----------------------------------------------------------------------------------------------------------");*/
		}
	}

	@After
	public void tearDown() {
		this.epServiceProvider.destroy();
	}

	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		if (newEvents != null) {
			System.out.println("New Events: ");
			for (EventBean event : newEvents) {
				System.out.println(event.getUnderlying());
			}
		}
		
		if (oldEvents != null) {
			System.out.println("Old Events: ");
			for (EventBean event : oldEvents) {
				System.out.println(event.getUnderlying());
			}
		}
	}
}
