package br.ufpe.cin.dsoa.epcenter.configuration.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.platform.event.impl.EsperProcessingService;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

public class EventParserSpec {

	private JAXBContext context;
	private Unmarshaller u;
	private EsperProcessingService epService;
	private EPServiceProvider esperProvider;
	private EventTypeList list;
	private List<String> validTypes = new ArrayList<String>();
	
	
	 <event type="DsoaEvent">
		<metadata>
			<property id="id" type="java.lang.String" />
			<property id="source" type="java.lang.String" />
			<property id="timestamp" type="java.lang.Double" />
		</metadata>
	</event>
	
	<event type="InvocationEvent" extends="DsoaEvent">
		<data>
			<property id="consumerId" type="java.lang.String" />
			<property id="serviceId" type="java.lang.String" />
			<property id="operationName" type="java.lang.String" />
			<property id="requestTimestamp" type="java.lang.Long" />
			<property id="responseTimestamp" type="java.lang.Long" />
			<property id="success" type="java.lang.Boolean" />
			<property id="exception" type="java.lang.Exception" />
		</data>
	</event>
	
	<event type="AvgResponseTimeEvent" extends="DsoaEvent">
		<data>
			<property id="value" type="java.lang.Double" />
		</data>
	</event>
	 

	@Before
	public void setUp() throws JAXBException, FileNotFoundException {
		this.context = JAXBContext.newInstance(EventTypeList.class);
		this.u = context.createUnmarshaller();
		this.list = (EventTypeList) u.unmarshal(new FileInputStream("src/test/resources/epcenter/configuration/event.xml"));
		this.esperProvider = EPServiceProviderManager.getProvider("Dsoa-EsperEngine", new Configuration());
		this.epService = new EsperProcessingService();
		this.epService.start();
		String[] types = {"DsoaEvent", "InvocationEvent", "AvgResponseTimeEvent" };
		this.validTypes = Arrays.asList(types);
		this.registerEvents();
		//this.defineInvocationEventType();
	}
	
	private void defineInvocationEventType(){
		List<PropertyType> metadata = new ArrayList<PropertyType>();
		List<PropertyType> data = new ArrayList<PropertyType>();

		//add metadata
		metadata.add(new PropertyType("id", String.class, true));
		metadata.add(new PropertyType("timestamp", Long.class, true));
		
		//add data
		data.add(new PropertyType("consumerId", String.class, false));
		data.add(new PropertyType("serviceId", String.class, true));
		data.add(new PropertyType("operationName", String.class, true));
		data.add(new PropertyType("resquestTimestamp", Long.class, true));
		data.add(new PropertyType("responseTimestamp", Long.class, true));
		data.add(new PropertyType("success", Boolean.class, true));
		data.add(new PropertyType("exception", Exception.class, false));
		
		//avgResponseTimeEventType = new EventType("InvocationEvent", metadata, data);
	}
	
	private void registerEvents() {
		List<EventType> eventTypeList = list.getEvents();
		if (list != null && !eventTypeList.isEmpty()) {
			List<EventType> subtypes = new ArrayList<EventType>();
			List<EventType> types = new ArrayList<EventType>();
			for (EventType eventType : eventTypeList) {
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
	public void testRegisteredEvents() {
		List<EventType> types = this.epService.getEventTypes();
		assertNotNull(types);
		for (EventType type : types) {
			assertNotNull(type.getName());
			assertNotNull(type.getMetadataMap());
			assertEquals(assertValidTypeName(type),Boolean.TRUE);
		}
	}
	
	public boolean assertValidTypeName(EventType type) {
		if (validTypes.contains(type.getName())) {
			return true;
		} else {
			return false;
		}
	}
	
	@Test
	public void testSendEvents() {
		EventType invocationEventType = this.epService.getEventType("InvocationEvent");
		Map<String, Property> metadataValue = new HashMap<String, Property>();
		Map<String, Property> dataValue = new HashMap<String, Property>();
		
		//metadata values
		metadataValue.put(invocationEventType.getMetadataPropertyType("id").getName(), new Property("123", invocationEventType.getMetadataPropertyType("id")));
		metadataValue.put(invocationEventType.getMetadataPropertyType("timestamp").getName(),new Property(System.nanoTime() , invocationEventType.getMetadataPropertyType("timestamp")));
		
		//data values
		dataValue.put(invocationEventType.getDataPropertyType("consumerId").getName(),new Property("consumer1", invocationEventType.getDataPropertyType("consumerId")));
		dataValue.put(invocationEventType.getDataPropertyType("serviceId").getName(),new Property("34", invocationEventType.getDataPropertyType("serviceId")));
		dataValue.put(invocationEventType.getDataPropertyType("operationName").getName(),new Property("fizz", invocationEventType.getDataPropertyType("operationName")));
		dataValue.put(invocationEventType.getDataPropertyType("requestTimestamp").getName(),new Property(1351L, invocationEventType.getDataPropertyType("requestTimestamp")));
		dataValue.put(invocationEventType.getDataPropertyType("responseTimestamp").getName(),new Property(2313L, invocationEventType.getDataPropertyType("responseTimestamp")));
		dataValue.put(invocationEventType.getDataPropertyType("success").getName(),new Property(true, invocationEventType.getDataPropertyType("success")));
		dataValue.put(invocationEventType.getDataPropertyType("exception").getName(),new Property(null, invocationEventType.getDataPropertyType("exception")));
		
		Event invocationEvent = new Event(invocationEventType, metadataValue, dataValue);
		
		EPStatement stmt = esperProvider.getEPAdministrator().createEPL("select event.metadata.timestamp, event.data.requestTimestamp from " + invocationEventType.getName() + " as event");
		EPStatement stmtInsert = esperProvider.getEPAdministrator().createEPL("insert into AvgResponseTimeEvent  " +
				"select (event.data.requestTimestamp - event.data.requestTimestamp) as value from " + invocationEventType.getName() + " as event");
		stmt.addListener(new InvocationEventListener());

		this.epService.publish(invocationEvent);
	}
	
	@Test
	public void testConfigPath() {
		assertEquals(EventTypeList.CONFIG, "DSOA-INF/event.xml");
	}

	@Test
	public void testDefaultEventType() {
		EventType e = list.getEvents().get(0);
		assertEquals("xml has modified", "DsoaEvent", e.getName());
	}

	@Test
	public void testEventList() throws JAXBException, FileNotFoundException {
		assertNotNull(list.getEvents());
	}

	@Test
	public void testEvetProperties() throws FileNotFoundException,
			JAXBException, ClassNotFoundException {
		Map<String, PropertyType> props = list.getEvents().get(0).getMetadataMap();
		assertNotNull(props);
	}
	
	class InvocationEventListener implements StatementAwareUpdateListener {

		@Override
		public void update(EventBean[] arg0, EventBean[] arg1,
				EPStatement arg2, EPServiceProvider arg3) {
			
			for(EventBean e : arg0){
				Object event = e.getUnderlying();
				com.espertech.esper.client.EventType esperType = e.getEventType();
				System.out.println(esperType.getPropertyDescriptor("name"));
				System.out.println(esperType.getPropertyDescriptor("metadata"));
				System.out.println(esperType.getPropertyDescriptor("data"));
				EventType avgEventType = epService.getEventType("AvgResponseTimeEvent");
				String name = avgEventType.getName();
				Map<String, PropertyType> metaProperties = avgEventType.getMetadataMap();
				for (String key : metaProperties.keySet()) {
					PropertyType propType = metaProperties.get(key);
					System.out.println(propType.getName());
					System.out.println(propType.getTypeName());
					System.out.println(propType.getExpression());
				}
				System.out.println(event);
			}
		}
	}

}
