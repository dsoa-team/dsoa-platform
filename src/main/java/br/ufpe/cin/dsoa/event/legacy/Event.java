package br.ufpe.cin.dsoa.event.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Event {
	private EventType eventType;
	private Map<String, Property> metadata;
	private Map<String, Property> data;
	
	public Event(EventType eventType,
			Map<String, Property> metadataProperties,
			Map<String, Property> dataProperties) {
		super();
		this.eventType = eventType;
		this.metadata = metadataProperties;
		this.data = dataProperties;
		validate();
	}

	public EventType getEventType() {
		return eventType;
	}

	public Property getProperty(String name) {
		Property property = null;
		if (name != null) {
			property = metadata.get(name);
			if (property == null) {
				property = data.get(name);
			}
		}
		return property;
	}
	
	private List<Property> getPropertyList(Map<String, Property> properties) {
		List<Property> attList = new ArrayList<Property>();
		if (properties != null) {
			attList.addAll(properties.values());
		}
		return attList;
	}
	
	public List<Property> getMetadataProperties() {
		return getPropertyList(metadata);
	}
	
	public List<Property> getDataProperties() {
		return getPropertyList(data);
	}
	
	private void validate() throws IllegalArgumentException {
		List<PropertyType> requiredHeaderTypes = eventType.getRequiredMetadataAttributeTypeList();
		List<PropertyType> optionalHeaderTypes = eventType.getOptionalMetadataAttributeTypeList();
		List<Property> headerAttributes = this.getMetadataProperties();
		
		List<PropertyType> requiredApplicationTypes = eventType.getRequiredDataAttributeTypeList();
		List<PropertyType> optionalApplicationTypes = eventType.getOptionalDataAttributeTypeList();
		List<Property> aplicationAttributes = this.getDataProperties();
		
		this.validate(requiredHeaderTypes, optionalHeaderTypes, headerAttributes);
		this.validate(requiredApplicationTypes, optionalApplicationTypes, aplicationAttributes);

		if ( !(requiredHeaderTypes.isEmpty() && requiredApplicationTypes.isEmpty()) ){
			throw new IllegalArgumentException("Invalid event! These attributes are required: Headers: "  + requiredHeaderTypes + " Application: " + requiredApplicationTypes);
		} else {
			if (!(headerAttributes.isEmpty() && aplicationAttributes.isEmpty()) ) {
				throw new IllegalArgumentException("Invalid event! These attributes are not declared: Headers: "  + headerAttributes + " Application: " + aplicationAttributes);
			}
		}
	}
	
	private void validate(List<PropertyType> requiredAttributeTypes, List<PropertyType> optionalAttributeTypes, List<Property> attList) {
		Iterator<Property> attItr = null;
		if (attList != null) {
			attItr = attList.iterator();
			while (attItr.hasNext()) {
				Property property = attItr.next();
				if (requiredAttributeTypes.contains(property.getAttributeType())) {
					requiredAttributeTypes.remove(property.getAttributeType());
					attItr.remove();
				} else if (optionalAttributeTypes.contains(property.getAttributeType())){
					optionalAttributeTypes.remove(property);
					attItr.remove();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Map<String, PropertyType> headerAttributeTypes	=	new HashMap<String, PropertyType>();
		PropertyType idType		= new PropertyType("id", String.class, true);
		PropertyType timestampType	= new PropertyType("timestamp", Long.class, true);
		headerAttributeTypes.put(idType.getName(), idType);
		headerAttributeTypes.put(timestampType.getName(), timestampType);
		
		Map<String, PropertyType> applicationAttributeTypes =	new HashMap<String, PropertyType>();
		PropertyType consumerIdType 		= new PropertyType("consumerId", String.class, true);
		PropertyType providerIdType 		= new PropertyType("providerId", String.class, true);
		PropertyType operationNameType 	= new PropertyType("operationName", String.class, true);
		PropertyType parameterTypesType 	= new PropertyType("parameterTypes", Class[].class, true);
		PropertyType parameterValuesType	= new PropertyType("parameterValues", Object[].class, true);
		PropertyType returnTypeType		= new PropertyType("returnType", Class.class, true);
		PropertyType returnValueType		= new PropertyType("returnValue", Object.class, true);
		PropertyType successType			= new PropertyType("success", Boolean.class, true);
		PropertyType exceptionType			= new PropertyType("exception", Exception.class, false);
		PropertyType resquestTimestampType	= new PropertyType("resquestTimestamp", Long.class, true);
		PropertyType responseTimestampType	= new PropertyType("responseTimestamp", Long.class, true);
		//applicationAttributeTypes.put(consumerIdType.getName(), consumerIdType);
		applicationAttributeTypes.put(providerIdType.getName(), providerIdType);
		applicationAttributeTypes.put(operationNameType.getName(), operationNameType);
		applicationAttributeTypes.put(parameterTypesType.getName(), parameterTypesType);
		applicationAttributeTypes.put(parameterValuesType.getName(), parameterValuesType);
		applicationAttributeTypes.put(returnTypeType.getName(), returnTypeType);
		applicationAttributeTypes.put(returnValueType.getName(), returnValueType);
		applicationAttributeTypes.put(successType.getName(), successType);
		applicationAttributeTypes.put(exceptionType.getName(), exceptionType);
		applicationAttributeTypes.put(resquestTimestampType.getName(), resquestTimestampType);
		applicationAttributeTypes.put(responseTimestampType.getName(), responseTimestampType);
		
		EventType invocationEventType = new EventType("InvocationEvent", headerAttributeTypes, applicationAttributeTypes);
		System.out.println(invocationEventType.toString());
		System.out.println(invocationEventType.getMetadataAttributeTypeList());
		System.out.println(invocationEventType.getRequiredMetadataAttributeTypeList());
		System.out.println(invocationEventType.getOptionalMetadataAttributeTypeList());
		
		System.out.println(invocationEventType.getDataAttributeTypeList());
		System.out.println(invocationEventType.getRequiredDataAttributeTypeList());
		System.out.println(invocationEventType.getOptionalDataAttributeTypeList());
		
		
		Map<String, Property> headerAttributes	=	new HashMap<String, Property>();
		Property id		= new Property("123", idType);
		Property timestamp	= new Property(System.nanoTime(), timestampType);
		headerAttributes.put(idType.getName(), id);
		headerAttributes.put(timestampType.getName(), timestamp);
		
		Map<String, Property> applicationAttributes =	new HashMap<String, Property>();
		Property consumerId 		= new Property("consumer1", consumerIdType);
		Property providerId 		= new Property("provider1", providerIdType);
		Property operationName 	= new Property("operationName", operationNameType);
		Property parameterTypes 	= new Property(new Class[] {String.class}, parameterTypesType);
		Property parameterValues	= new Property(new Object[] {"parameterValues"}, parameterValuesType);
		Property returnType		= new Property(Long.class, returnTypeType);
		Property returnValue		= new Property(100, returnValueType);
		Property success			= new Property(true, successType);
		Property exception			= new Property(null, exceptionType);
		Property resquestTimestamp	= new Property(System.nanoTime(), resquestTimestampType);
		Property responseTimestamp	= new Property(System.nanoTime(), responseTimestampType);
		applicationAttributes.put(consumerIdType.getName(), consumerId);
		applicationAttributes.put(providerIdType.getName(), providerId);
		applicationAttributes.put(operationNameType.getName(), operationName);
		applicationAttributes.put(consumerIdType.getName(), consumerId);
		applicationAttributes.put(parameterTypesType.getName(), parameterTypes);
		applicationAttributes.put(parameterValuesType.getName(), parameterValues);
		applicationAttributes.put(returnTypeType.getName(), returnType);
		applicationAttributes.put(returnValueType.getName(), returnValue);
		applicationAttributes.put(successType.getName(), success);
		applicationAttributes.put(exceptionType.getName(), exception);
		applicationAttributes.put(resquestTimestampType.getName(), resquestTimestamp);
		applicationAttributes.put(responseTimestampType.getName(), responseTimestamp);
		
		Event invocationEvent = new Event(invocationEventType, headerAttributes, applicationAttributes);
	}
}	
