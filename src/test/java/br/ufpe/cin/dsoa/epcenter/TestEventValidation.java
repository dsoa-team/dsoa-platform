package br.ufpe.cin.dsoa.epcenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.ufpe.cin.dsoa.event.meta.Event;
import br.ufpe.cin.dsoa.event.meta.EventType;
import br.ufpe.cin.dsoa.event.meta.Property;
import br.ufpe.cin.dsoa.event.meta.PropertyType;

public class TestEventValidation {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
	
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
		//data.add(new PropertyType("parameterTypes", Class[].class,	true));
		//data.add(new PropertyType("parameterValues", Object[].class, true));
		data.add(new PropertyType("returnType", Class.class, true));
		data.add(new PropertyType("returnValue", Object.class, true));
		data.add(new PropertyType("success", Boolean.class, true));
		data.add(new PropertyType("exception", Exception.class, false));
		
		EventType invocationEventType = new EventType("InvocationEvent", metadata, data);
		
		System.out.println(invocationEventType.toString());
		System.out.println(invocationEventType.getMetadataList());
		System.out.println(invocationEventType.getRequiredMetadataAttributeTypeList());
		System.out.println(invocationEventType.getOptionalMetadataAttributeTypeList());
		
		System.out.println(invocationEventType.getDataList());
		System.out.println(invocationEventType.getRequiredDataAttributeTypeList());
		System.out.println(invocationEventType.getOptionalDataAttributeTypeList());
		

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
	}

}
