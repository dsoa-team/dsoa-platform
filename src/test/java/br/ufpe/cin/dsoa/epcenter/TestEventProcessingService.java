package br.ufpe.cin.dsoa.epcenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.dsoa.event.InvocationEvent;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.platform.event.impl.EsperProcessingService;
import br.ufpe.cin.dsoa.platform.event.impl.Statement;

public class TestEventProcessingService implements NotificationListener {

	EsperProcessingService epCenter;
	String[] providers = { "provider-1", "provider-2", "provider-3" };
	String[] services = { "service-1", "service-2", "service-3", "service-4", "service-5" };
	String[] operations = { "foo", "bar" };
	int[] delays = { 1, 2, 3, 4, 5 };

	/*
	 * <agent name="ResponseTimeAgent" description="Response time calculator agent">
	 * 		<category name="qos"/> 
	 * 		<input event="InvocationEvent" alias="invocation">
	 * 			<window type="length_batch" size="100"/>
	 * 			<filters>
	 * 				<filter name="service" type="variable"/>
	 * 				<filter name="operation" type="variable"/>
	 * 			</filters>
	 * 		</input> 
	 * 		<output event="AvgResponseTime" alias="avgResp">
	 * 			<properties>
	 * 				<property name="value">
	 * 					avg(invocation.responseTimestamp - invocation.requestTimestamp)
	 * 				</property>
	 * 			</properties> 
	 *		</output>
	 * </agent>
	 * 
	 * 
	 * <metric>
	 * 		<category>qos</category>
	 * 		<name>AvgResponseTime</name>
	 * 		<description>Average response time</description>
	 * 		<event type="ResponseTime">
 * 				<value>
 * 					<statistic>avg</statistic>
 * 					<property>value</property>
 * 				</value>
 * 				<window type="length">
 * 					300
 * 				</window>
	 *		</event>
	 * </metric>
	 * 
	 * 
	 * insert into ResponseTime(value) 
	 * select avg(invocation.responseTimestamp - invocation.requestTimestamp)
	 * from InvocationEvent( 
	 */

	@Before
	/*public void setUp() {
		epCenter = new EsperProcessingService();
		epCenter.start();
		Statement stmt1, stmt2, stmt3, stmt4;
		stmt1 = new Statement("stmt1", "insert into ResponseTime(service, operation, time) "
				+ "select service, operation, responseTimestamp - requestTimestamp "
				+ "from InvocationEvent");
		epCenter.defineStatement(stmt1);
		
		stmt3 = new Statement("stmt3", "insert into Availability(service, operation, success) "
				+ "select service, operation, success "
				+ "from InvocationEvent");
		epCenter.defineStatement(stmt3);

		stmt2 = new Statement("stmt2", "select service, operation, avg(time) from ResponseTime(service=?, operation=?).win:length_batch(4)  group by service, operation");
		epCenter.defineStatement(stmt2.getName(), stmt2.getQuery());
		
		stmt4 = new Statement("stmt4", "select ResponseTime.service, ResponseTime.operation, success, avg(time) " +
				"from ResponseTime(service=?, operation=?).win:length_batch(6), " +
				"Availability(service=?, operation=?, success=true).win:length_batch(3) " +
				"group by ResponseTime.service, ResponseTime.operation, success");
		
		epCenter.defineStatement(stmt4.getName(), stmt4.getQuery());
		
		List<Object> parameters = new ArrayList<Object>();
		parameters.add("service-1");
		parameters.add("foo");
		parameters.add("service-1");
		parameters.add("foo");
		//epCenter.subscribe(stmt2.getName(), parameters , this);
		
		epCenter.subscribe(stmt4.getName(), parameters, this);
	}*/

	@Test
	public void testConfigPath() {
/*		epCenter = new EsperProcessingService();
		epCenter.start();
		Statement stmt1, stmt2, stmt3;
		stmt1 = new Statement("stmt1", "insert into ResponseTime(service, operation, time) "
				+ "select service, operation, avg(responseTimestamp - requestTimestamp)"
				+ "from InvocationEvent.win:length_batch(4) " + "group by service, operation");
		epCenter.defineStatement(stmt1);

		stmt2 = new Statement("stmt2", "select * from ResponseTime");
		epCenter.defineStatement(stmt2);

		epCenter.subscribe(stmt2.getName(), (NotificationListener) this);*/
		
		/*String provider;
		String service;
		String operation;
		boolean success;
		long requestTimestamp;
		long responseTimestamp;

		Random random = new Random();

		for (int i = 0; i < 100; i++) {
			provider = providers[random.nextInt(providers.length)];
			service = services[random.nextInt(services.length)];
			operation = operations[random.nextInt(operations.length)];
			success = true;
			requestTimestamp = System.currentTimeMillis();
			responseTimestamp = System.currentTimeMillis() + delays[random.nextInt(delays.length)];
			System.out.println("Service: " + service);
			System.out.println("Operation: " + operation);
			System.out.println("Latency: " + (responseTimestamp - requestTimestamp));
			InvocationEvent invocation = new InvocationEvent(provider, service, operation, success, requestTimestamp,
					responseTimestamp);
			epCenter.publishEvent(invocation);
		}*/
	}

	public void receive(Map result, Object userObject, String statementName) {
		// TODO Auto-generated method stub
		System.out.println(result);
	}

	public void receive(Object result, String statementName) {
		// TODO Auto-generated method stub
		System.out.println("Stmt: " + statementName);
		System.out.println("Result: " + result);
	}
}
