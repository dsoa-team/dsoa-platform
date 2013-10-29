package br.ufpe.cin.dsoa.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

public class QueryBuilderTest {

	public static void main(String[] args) {
		Configuration configuration = new Configuration();
		configuration.getEngineDefaults().getThreading().setInsertIntoDispatchPreserveOrder(false);
		configuration.getEngineDefaults().getThreading().setListenerDispatchPreserveOrder(false);
		Event event = createSampleInvocationEvent();
		EventType eventType = event.getEventType();
		Map<String, Object> definitionMap = event.getEventType().toDefinitionMap();
		configuration.addEventType(eventType.getName(), definitionMap);
		
		EPServiceProvider provider = EPServiceProviderManager.getProvider("Dsoa-EsperEngine",
				configuration);
		EPStatement stmt = provider
				.getEPAdministrator()
				.createEPL(getQuery());
		stmt.addListener(new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] events, EventBean[] arg1, EPStatement arg2,
					EPServiceProvider arg3) {
				for (EventBean event : events) {
					System.out.println("Event type: " + event.getEventType());
				}
			}
		});
		
		String name = event.getEventType().getName();
		Map<String, Object> eventMap = event.toMap();		
		EPRuntime runtime = provider.getEPRuntime();
		runtime.sendEvent(eventMap, name);
	}

	private static String getQuery() {
		return " SELECT  event.metadata_id as metadata_id , "
				+ " event.metadata_timestamp as metadata_timestamp , "
				+ " event.metadata_source as metadata_src , "
				+ " avg(event.data_responseTimestamp - event.data_requestTimestamp) as data_value "
				+ " FROM InvocationEvent(event.data_consumerId = 'HomebrokerClient').win:length(10) as event "
				+ " HAVING (event.metadata_source='102.priceAlert' AND avg(event.data_responseTimestamp - event.data_requestTimestamp)>200.0)";
	}

	private static Event createSampleInvocationEvent() {

		Map<String, Property> metadataValue = new HashMap<String, Property>();
		Map<String, Property> dataValue = new HashMap<String, Property>();
		EventType invocationEventType = defineInvocationEventType();

		// metadata values
		metadataValue.put(invocationEventType.getMetadataPropertyType("id").getName(),
				new Property("123", invocationEventType.getMetadataPropertyType("id")));
		metadataValue.put(
				invocationEventType.getMetadataPropertyType("timestamp").getName(),
				new Property(System.nanoTime(), invocationEventType
						.getMetadataPropertyType("timestamp")));
		metadataValue.put(invocationEventType.getMetadataPropertyType("source").getName(),
				new Property("localhost", invocationEventType.getMetadataPropertyType("source")));

		// data values
		dataValue.put(invocationEventType.getDataPropertyType("consumerId").getName(),
				new Property("consumer1", invocationEventType.getDataPropertyType("consumerId")));
		dataValue.put(invocationEventType.getDataPropertyType("serviceId").getName(), new Property(
				"34", invocationEventType.getDataPropertyType("serviceId")));
		dataValue.put(invocationEventType.getDataPropertyType("operationName").getName(),
				new Property("fizz", invocationEventType.getDataPropertyType("operationName")));
		dataValue.put(invocationEventType.getDataPropertyType("requestTimestamp").getName(),
				new Property(1351L, invocationEventType.getDataPropertyType("requestTimestamp")));
		dataValue.put(invocationEventType.getDataPropertyType("responseTimestamp").getName(),
				new Property(2313L, invocationEventType.getDataPropertyType("responseTimestamp")));
		// dataValue.put(invocationEventType.getDataPropertyType("parameterTypes").getName(),new
		// Property("parameterTypes",
		// invocationEventType.getDataPropertyType("parameterTypes")));
		// dataValue.put(invocationEventType.getDataPropertyType("parameterValues").getName(),new
		// Property("parameterValues",
		// invocationEventType.getDataPropertyType("parameterValues")));
		// dataValue.put(invocationEventType.getDataPropertyType("returnType").getName(),new
		// Property(String.class,
		// invocationEventType.getDataPropertyType("returnType")));
		// dataValue.put(invocationEventType.getDataPropertyType("returnValue").getName(),new
		// Property("return",
		// invocationEventType.getDataPropertyType("returnValue")));
		dataValue.put(invocationEventType.getDataPropertyType("success").getName(), new Property(
				true, invocationEventType.getDataPropertyType("success")));
		dataValue.put(invocationEventType.getDataPropertyType("exception").getName(), new Property(
				null, invocationEventType.getDataPropertyType("exception")));

		Event invocationEvent = new Event(invocationEventType, metadataValue, dataValue);

		return invocationEvent;
	}

	private static EventType defineInvocationEventType() {
		List<PropertyType> metadata = new ArrayList<PropertyType>();
		List<PropertyType> data = new ArrayList<PropertyType>();

		// add metadata
		metadata.add(new PropertyType("timestamp", Long.class, true));
		metadata.add(new PropertyType("id", String.class, true));
		metadata.add(new PropertyType("source", String.class, true));

		// add data
		data.add(new PropertyType("consumerId", String.class, false));
		data.add(new PropertyType("serviceId", String.class, true));
		data.add(new PropertyType("operationName", String.class, true));
		data.add(new PropertyType("requestTimestamp", Long.class, true));
		data.add(new PropertyType("responseTimestamp", Long.class, true));
		// data.add(new PropertyType("parameterTypes", Class[].class, true));
		// data.add(new PropertyType("parameterValues", Object[].class, true));
		// data.add(new PropertyType("returnType", Class.class, true));
		// data.add(new PropertyType("returnValue", Object.class, true));
		data.add(new PropertyType("success", Boolean.class, true));
		data.add(new PropertyType("exception", Exception.class, false));

		EventType invocationEventType = new EventType("InvocationEvent", metadata, data);

		return invocationEventType;
	}

}
