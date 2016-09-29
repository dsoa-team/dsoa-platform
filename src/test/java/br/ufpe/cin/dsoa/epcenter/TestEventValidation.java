package br.ufpe.cin.dsoa.epcenter;

import org.junit.Before;
import org.junit.Test;

public class TestEventValidation {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
	
		/*List<PropertyType> metadata = new ArrayList<PropertyType>();
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
		
		//EventType avgResponseTimeEventType = new EventType("InvocationEvent", metadata, data);
		
		System.out.println(avgResponseTimeEventType.toString());
		System.out.println(avgResponseTimeEventType.getMetadataList());
		System.out.println(avgResponseTimeEventType.getRequiredMetadataAttributeTypeList());
		System.out.println(avgResponseTimeEventType.getOptionalMetadataAttributeTypeList());
		
		System.out.println(avgResponseTimeEventType.getDataList());
		System.out.println(avgResponseTimeEventType.getRequiredDataAttributeTypeList());
		System.out.println(avgResponseTimeEventType.getOptionalDataAttributeTypeList());
		

		Map<String, Property> metadataValue = new HashMap<String, Property>();
		Map<String, Property> dataValue = new HashMap<String, Property>();
		
		//metadata values
		metadataValue.put(avgResponseTimeEventType.getMetadataPropertyType("id").getName(), new Property("123", avgResponseTimeEventType.getMetadataPropertyType("id")));
		metadataValue.put(avgResponseTimeEventType.getMetadataPropertyType("timestamp").getName(),new Property(System.nanoTime() , avgResponseTimeEventType.getMetadataPropertyType("timestamp")));
		
		//data values
		dataValue.put(avgResponseTimeEventType.getDataPropertyType("consumerId").getName(),new Property("consumer1", avgResponseTimeEventType.getDataPropertyType("consumerId")));
		dataValue.put(avgResponseTimeEventType.getDataPropertyType("serviceId").getName(),new Property("34", avgResponseTimeEventType.getDataPropertyType("serviceId")));
		dataValue.put(avgResponseTimeEventType.getDataPropertyType("operationName").getName(),new Property("fizz", avgResponseTimeEventType.getDataPropertyType("operationName")));
		dataValue.put(avgResponseTimeEventType.getDataPropertyType("resquestTimestamp").getName(),new Property(1351L, avgResponseTimeEventType.getDataPropertyType("resquestTimestamp")));
		dataValue.put(avgResponseTimeEventType.getDataPropertyType("responseTimestamp").getName(),new Property(2313L, avgResponseTimeEventType.getDataPropertyType("responseTimestamp")));
		//dataValue.put(avgResponseTimeEventType.getDataPropertyType("parameterTypes").getName(),new Property("parameterTypes", avgResponseTimeEventType.getDataPropertyType("parameterTypes")));
		//dataValue.put(avgResponseTimeEventType.getDataPropertyType("parameterValues").getName(),new Property("parameterValues", avgResponseTimeEventType.getDataPropertyType("parameterValues")));
		dataValue.put(avgResponseTimeEventType.getDataPropertyType("returnType").getName(),new Property(String.class, avgResponseTimeEventType.getDataPropertyType("returnType")));
		dataValue.put(avgResponseTimeEventType.getDataPropertyType("returnValue").getName(),new Property("return", avgResponseTimeEventType.getDataPropertyType("returnValue")));
		dataValue.put(avgResponseTimeEventType.getDataPropertyType("success").getName(),new Property(true, avgResponseTimeEventType.getDataPropertyType("success")));
		dataValue.put(avgResponseTimeEventType.getDataPropertyType("exception").getName(),new Property(null, avgResponseTimeEventType.getDataPropertyType("exception")));
		
		Event invocationEvent = new Event(avgResponseTimeEventType, metadataValue, dataValue);*/
	}

}
