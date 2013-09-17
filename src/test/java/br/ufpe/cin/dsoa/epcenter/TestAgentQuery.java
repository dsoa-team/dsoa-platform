package br.ufpe.cin.dsoa.epcenter;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.agent.AgentList;
import br.ufpe.cin.dsoa.platform.event.impl.EsperProcessingService;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

public class TestAgentQuery {
	private EsperProcessingService epService;
	private EventTypeList eventList;
	private AgentList agentList;

	@Before
	public void setUp() throws Exception {
		JAXBContext context = JAXBContext.newInstance(EventTypeList.class);
		Unmarshaller u = context.createUnmarshaller();
		this.eventList = (EventTypeList) u.unmarshal(new FileInputStream(
				"src/test/resources/epcenter/configuration/event.xml"));

		context = JAXBContext.newInstance(AgentList.class);
		u = context.createUnmarshaller();
		this.agentList = (AgentList) u.unmarshal(new FileInputStream(
				"src/test/resources/epcenter/configuration/agent.xml"));

		epService = new EsperProcessingService();
		epService.start();

		handleEventDefinitions();
	}

	private void handleEventDefinitions() {
		List<EventType> list = eventList.getEvents();
		if (list != null && !list.isEmpty()) {
			List<EventType> subtypes = new ArrayList<EventType>();
			List<EventType> types = new ArrayList<EventType>();
			for (EventType eventType : eventList.getEvents()) {
				if (eventType.getSuperTypeName() != null) {
					subtypes.add(eventType);
				} else {
					types.add(eventType);
				}
			}

			if (!types.isEmpty()) {
				for (EventType type : types) {
					epService.registerEventType(type);
				}
			}

			if (!subtypes.isEmpty()) {
				for (EventType subtype : subtypes) {
						EventType superType = epService.getEventType(subtype.getSuperTypeName());
						if (superType != null) {
							Map<String, PropertyType> superMetaProps = superType.getMetadataMap();
							Map<String, PropertyType> subMetadataProps = subtype.getMetadataMap();
							copyProperties(superMetaProps, subMetadataProps);
	
							Map<String, PropertyType> superDataProps = superType.getDataMap();
							Map<String, PropertyType> subDataProps = subtype.getDataMap();
							copyProperties(superDataProps, subDataProps);
						}
						epService.registerEventType(subtype);
					}
			}
		}

	}

	private void copyProperties(Map<String, PropertyType> superProps, Map<String, PropertyType> subProps) {
		if (!superProps.isEmpty()) {
			Set<String> keys = superProps.keySet();
			for (String key : keys) {
				subProps.put(key, superProps.get(key));
			}
		}
	}

	@Test
	public void testAgent() {
		EPServiceProvider provider = this.epService.getEpProvider();
		
		//epService.registerEventType(defineAvgResponseTimeType());
		
		// subscription
		EPStatement stmt = provider.getEPAdministrator().createEPL(
				"INSERT INTO AvgResponseTimeType SELECT event.type as type, event.metadata.id as metadata_id, event.metadata.source as metadata_source, avg(event.data.responseTimestamp-event.data.requestTimestamp) as data_avg FROM "
						+ " InvocationEvent as event");
		stmt.addListener(new EventListener());

		 this.epService.publish(createSampleInvocationEvent());
	}
	
	
	private EventType defineInvocationEventType(){
		List<PropertyType> metadata = new ArrayList<PropertyType>();
		List<PropertyType> data = new ArrayList<PropertyType>();

		//add metadata
		metadata.add(new PropertyType("timestamp", Long.class, true));
		metadata.add(new PropertyType("id", String.class, true));
		metadata.add(new PropertyType("source", String.class, true));
		
		//add data
		data.add(new PropertyType("consumerId", String.class, false));
		data.add(new PropertyType("serviceId", String.class, true));
		data.add(new PropertyType("operationName", String.class, true));
		data.add(new PropertyType("requestTimestamp", Long.class, true));
		data.add(new PropertyType("responseTimestamp", Long.class, true));
		//data.add(new PropertyType("parameterTypes", Class[].class,	true));
		//data.add(new PropertyType("parameterValues", Object[].class, true));
		//data.add(new PropertyType("returnType", Class.class, true));
		//data.add(new PropertyType("returnValue", Object.class, true));
		data.add(new PropertyType("success", Boolean.class, true));
		data.add(new PropertyType("exception", Exception.class, false));
		
		EventType invocationEventType = new EventType("InvocationEvent", metadata, data);
		
		return invocationEventType;
	}
	
	private EventType defineAvgResponseTimeType(){
		List<PropertyType> metadata = new ArrayList<PropertyType>();
		List<PropertyType> data = new ArrayList<PropertyType>();

		//add metadata
		metadata.add(new PropertyType("timestamp", Long.class, true));
		metadata.add(new PropertyType("id", String.class, true));
		metadata.add(new PropertyType("source", String.class, true));
		
		//add data
		data.add(new PropertyType("value", Long.class, false));
		
		EventType avgResponseTimeType = new EventType("AvgResponseTimeType", metadata, data);
		
		return avgResponseTimeType;
	}
	
	private Event createSampleInvocationEvent(){
		
		
		Map<String, Property> metadataValue = new HashMap<String, Property>();
		Map<String, Property> dataValue = new HashMap<String, Property>();
		EventType invocationEventType =  defineInvocationEventType();
		
		//metadata values
		metadataValue.put(invocationEventType.getMetadataPropertyType("id").getName(), new Property("123", invocationEventType.getMetadataPropertyType("id")));
		metadataValue.put(invocationEventType.getMetadataPropertyType("timestamp").getName(),new Property(System.nanoTime() , invocationEventType.getMetadataPropertyType("timestamp")));
		metadataValue.put(invocationEventType.getMetadataPropertyType("source").getName(),new Property("localhost" , invocationEventType.getMetadataPropertyType("source")));
		
		
		//data values
		dataValue.put(invocationEventType.getDataPropertyType("consumerId").getName(),new Property("consumer1", invocationEventType.getDataPropertyType("consumerId")));
		dataValue.put(invocationEventType.getDataPropertyType("serviceId").getName(),new Property("34", invocationEventType.getDataPropertyType("serviceId")));
		dataValue.put(invocationEventType.getDataPropertyType("operationName").getName(),new Property("fizz", invocationEventType.getDataPropertyType("operationName")));
		dataValue.put(invocationEventType.getDataPropertyType("requestTimestamp").getName(),new Property(1351L, invocationEventType.getDataPropertyType("requestTimestamp")));
		dataValue.put(invocationEventType.getDataPropertyType("responseTimestamp").getName(),new Property(2313L, invocationEventType.getDataPropertyType("responseTimestamp")));
		//dataValue.put(invocationEventType.getDataPropertyType("parameterTypes").getName(),new Property("parameterTypes", invocationEventType.getDataPropertyType("parameterTypes")));
		//dataValue.put(invocationEventType.getDataPropertyType("parameterValues").getName(),new Property("parameterValues", invocationEventType.getDataPropertyType("parameterValues")));
		//dataValue.put(invocationEventType.getDataPropertyType("returnType").getName(),new Property(String.class, invocationEventType.getDataPropertyType("returnType")));
		//dataValue.put(invocationEventType.getDataPropertyType("returnValue").getName(),new Property("return", invocationEventType.getDataPropertyType("returnValue")));
		dataValue.put(invocationEventType.getDataPropertyType("success").getName(),new Property(true, invocationEventType.getDataPropertyType("success")));
		dataValue.put(invocationEventType.getDataPropertyType("exception").getName(),new Property(null, invocationEventType.getDataPropertyType("exception")));
		
		Event invocationEvent = new Event(invocationEventType, metadataValue, dataValue);
		
		return invocationEvent;
	}

	class EventListener implements StatementAwareUpdateListener {

		@Override
		public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2, EPServiceProvider arg3) {

			for (EventBean e : arg0) {
				Object event = e.getUnderlying();
				if (event instanceof Event) {
					Event invocationEvent = (Event) event;
					System.out.println(invocationEvent);
				} else {
					System.out.println(event);
				}
			}
		}
	}

}
