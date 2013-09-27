package br.ufpe.cin.dsoa.epcenter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.agent.AgentList;
import br.ufpe.cin.dsoa.platform.event.impl.EsperProcessingService;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

public class TestEventGeneration {

	List<PropertyType> metadata;
	List<PropertyType> data;
	EventType invocationEventType;
	EsperProcessingService epService;
	private EPServiceProvider esperProvider;
	

	@Before
	public void setUp() throws Exception {
		metadata = new ArrayList<PropertyType>();
		data = new ArrayList<PropertyType>();
		this.defineInvocationEventType();
		this.esperProvider = EPServiceProviderManager.getProvider("Dsoa-EsperEngine", new Configuration());
		//epService = new EsperProcessingService();//these tests will fail
		epService.start();
	}

	public void testGeneration() {
		
		
		Map<String, Property> metadataValue = new HashMap<String, Property>();
		Map<String, Property> dataValue = new HashMap<String, Property>();
		
		//metadata values
		metadataValue.put(invocationEventType.getMetadataPropertyType("id").getName(), new Property("123", invocationEventType.getMetadataPropertyType("id")));
		metadataValue.put(invocationEventType.getMetadataPropertyType("timestamp").getName(),new Property(System.nanoTime() , invocationEventType.getMetadataPropertyType("timestamp")));
		
		//data values
		dataValue.put(invocationEventType.getDataPropertyType("consumerId").getName(),new Property("consumer1", invocationEventType.getDataPropertyType("consumerId")));
		dataValue.put(invocationEventType.getDataPropertyType("serviceId").getName(),new Property("34", invocationEventType.getDataPropertyType("serviceId")));
		dataValue.put(invocationEventType.getDataPropertyType("operationName").getName(),new Property("fizz", invocationEventType.getDataPropertyType("operationName")));
		dataValue.put(invocationEventType.getDataPropertyType("resquestTimestamp").getName(),new Property(1351L, invocationEventType.getDataPropertyType("resquestTimestamp")));
		dataValue.put(invocationEventType.getDataPropertyType("responseTimestamp").getName(),new Property(2313L, invocationEventType.getDataPropertyType("responseTimestamp")));
		//dataValue.put(invocationEventType.getDataPropertyType("parameterTypes").getName(),new Property("parameterTypes", invocationEventType.getDataPropertyType("parameterTypes")));
		//dataValue.put(invocationEventType.getDataPropertyType("parameterValues").getName(),new Property("parameterValues", invocationEventType.getDataPropertyType("parameterValues")));
		dataValue.put(invocationEventType.getDataPropertyType("returnType").getName(),new Property(String.class, invocationEventType.getDataPropertyType("returnType")));
		dataValue.put(invocationEventType.getDataPropertyType("returnValue").getName(),new Property("return", invocationEventType.getDataPropertyType("returnValue")));
		dataValue.put(invocationEventType.getDataPropertyType("success").getName(),new Property(true, invocationEventType.getDataPropertyType("success")));
		dataValue.put(invocationEventType.getDataPropertyType("exception").getName(),new Property(null, invocationEventType.getDataPropertyType("exception")));
		
		Event invocationEvent = new Event(invocationEventType, metadataValue, dataValue);
		
		
		Map<String, Object> definition = invocationEventType.toDefinitionMap();
		String name = invocationEventType.getName();
		
		Map<String, Object> event =  invocationEvent.toMap();
		
		esperProvider.getEPAdministrator().getConfiguration().addEventType(name, definition);
		
		EPStatement stmt = esperProvider.getEPAdministrator().createEPL("select event.metadata.timestamp, event.data.resquestTimestamp from " + name + " as event");
		stmt.addListener(new InvocationEventListener());
		esperProvider.getEPRuntime().sendEvent(event, name);
	}
	
	
	@Test
	public void testEpService() {
		
		Map<String, Property> metadataValue = new HashMap<String, Property>();
		Map<String, Property> dataValue = new HashMap<String, Property>();
		
		//metadata values
		metadataValue.put(invocationEventType.getMetadataPropertyType("id").getName(), new Property("123", invocationEventType.getMetadataPropertyType("id")));
		metadataValue.put(invocationEventType.getMetadataPropertyType("timestamp").getName(),new Property(System.nanoTime() , invocationEventType.getMetadataPropertyType("timestamp")));
		
		//data values
		dataValue.put(invocationEventType.getDataPropertyType("consumerId").getName(),new Property("consumer1", invocationEventType.getDataPropertyType("consumerId")));
		dataValue.put(invocationEventType.getDataPropertyType("serviceId").getName(),new Property("34", invocationEventType.getDataPropertyType("serviceId")));
		dataValue.put(invocationEventType.getDataPropertyType("operationName").getName(),new Property("fizz", invocationEventType.getDataPropertyType("operationName")));
		dataValue.put(invocationEventType.getDataPropertyType("resquestTimestamp").getName(),new Property(1351L, invocationEventType.getDataPropertyType("resquestTimestamp")));
		dataValue.put(invocationEventType.getDataPropertyType("responseTimestamp").getName(),new Property(2313L, invocationEventType.getDataPropertyType("responseTimestamp")));
		//dataValue.put(invocationEventType.getDataPropertyType("parameterTypes").getName(),new Property("parameterTypes", invocationEventType.getDataPropertyType("parameterTypes")));
		//dataValue.put(invocationEventType.getDataPropertyType("parameterValues").getName(),new Property("parameterValues", invocationEventType.getDataPropertyType("parameterValues")));
		dataValue.put(invocationEventType.getDataPropertyType("returnType").getName(),new Property(String.class, invocationEventType.getDataPropertyType("returnType")));
		dataValue.put(invocationEventType.getDataPropertyType("returnValue").getName(),new Property("return", invocationEventType.getDataPropertyType("returnValue")));
		dataValue.put(invocationEventType.getDataPropertyType("success").getName(),new Property(true, invocationEventType.getDataPropertyType("success")));
		dataValue.put(invocationEventType.getDataPropertyType("exception").getName(),new Property(null, invocationEventType.getDataPropertyType("exception")));
		
		Event invocationEvent = new Event(invocationEventType, metadataValue, dataValue);
		String name = invocationEventType.getName();

		this.epService.registerEventType(this.invocationEventType);

		//subscription
		EPStatement stmt = esperProvider.getEPAdministrator().createEPL("select event.metadata.timestamp, event.data.resquestTimestamp from " + name + " as event");
		stmt.addListener(new InvocationEventListener());
	
		this.epService.publish(invocationEvent);
	}
	
	class InvocationEventListener implements StatementAwareUpdateListener {

		@Override
		public void update(EventBean[] arg0, EventBean[] arg1,
				EPStatement arg2, EPServiceProvider arg3) {
			
			for(EventBean e : arg0){
				Object event = e.getUnderlying();
				if(event instanceof Event){
					Event invocationEvent = (Event) event;
					System.out.println(invocationEvent);
				} else {
					System.out.println(event);
				}
			}
		}
	}
	
	private void defineInvocationEventType(){
		metadata = new ArrayList<PropertyType>();
		data = new ArrayList<PropertyType>();

		//add metadata
		metadata.add(new PropertyType("id", String.class, true));
		metadata.add(new PropertyType("timestamp", Long.class, true));
		
		//add data
		data.add(new PropertyType("consumerId", String.class, false));
		data.add(new PropertyType("serviceId", String.class, true));
		data.add(new PropertyType("operationName", String.class, true));
		data.add(new PropertyType("resquestTimestamp", Long.class, true));
		data.add(new PropertyType("responseTimestamp", Long.class, true));
		//data.add(new PropertyType("parameterTypes", Class[].class,	true));
		//data.add(new PropertyType("parameterValues", Object[].class, true));
		data.add(new PropertyType("returnType", Class.class, true));
		data.add(new PropertyType("returnValue", Object.class, true));
		data.add(new PropertyType("success", Boolean.class, true));
		data.add(new PropertyType("exception", Exception.class, false));
		
		//invocationEventType = new EventType("InvocationEvent", metadata, data);
	}
	
	@Test
	public void getProcessingAgents() throws JAXBException, FileNotFoundException {
		
		JAXBContext context = JAXBContext.newInstance(AgentList.class);
		Unmarshaller u = context.createUnmarshaller();
		AgentList list = (AgentList) u.unmarshal(new FileInputStream(
				"src/test/resources/epcenter/configuration/agent.xml"));
		
		//return list;
	}

}
