package br.ufpe.cin.dsoa.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Event {
	private EventType eventType;
	private Map<String, Attribute> headerAttributes;
	private Map<String, Attribute> applicationAttributes;
	
	public Event(EventType eventType,
			Map<String, Attribute> headerAttributes,
			Map<String, Attribute> applicationAttributes) {
		super();
		this.eventType = eventType;
		this.headerAttributes = headerAttributes;
		this.applicationAttributes = applicationAttributes;
		validate();
	}

	public EventType getEventType() {
		return eventType;
	}

	public Attribute getAttribute(String name) {
		Attribute attribute = null;
		if (name != null) {
			attribute = headerAttributes.get(name);
			if (attribute == null) {
				attribute = applicationAttributes.get(name);
			}
		}
		return attribute;
	}
	
	private List<Attribute> getAttributeList(Map<String, Attribute> attributes) {
		List<Attribute> attList = new ArrayList<Attribute>();
		if (attributes != null) {
			attList.addAll(attributes.values());
		}
		return attList;
	}
	
	public List<Attribute> getHeaderAttributes() {
		return getAttributeList(headerAttributes);
	}
	
	public List<Attribute> getApplicationAttributes() {
		return getAttributeList(applicationAttributes);
	}
	
	private void validate() throws IllegalArgumentException {
		List<AttributeType> requiredHeaderTypes = eventType.getRequiredHeaderAttributeTypeList();
		List<AttributeType> optionalHeaderTypes = eventType.getOptionalHeaderAttributeTypeList();
		List<Attribute> headerAttributes = this.getHeaderAttributes();
		
		List<AttributeType> requiredApplicationTypes = eventType.getRequiredApplicationAttributeTypeList();
		List<AttributeType> optionalApplicationTypes = eventType.getOptionalApplicationAttributeTypeList();
		List<Attribute> aplicationAttributes = this.getApplicationAttributes();
		
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
	
	private void validate(List<AttributeType> requiredAttributeTypes, List<AttributeType> optionalAttributeTypes, List<Attribute> attList) {
		Iterator<Attribute> attItr = null;
		if (attList != null) {
			attItr = attList.iterator();
			while (attItr.hasNext()) {
				Attribute attribute = attItr.next();
				if (requiredAttributeTypes.contains(attribute.getAttributeType())) {
					requiredAttributeTypes.remove(attribute.getAttributeType());
					attItr.remove();
				} else if (optionalAttributeTypes.contains(attribute.getAttributeType())){
					optionalAttributeTypes.remove(attribute);
					attItr.remove();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Map<String, AttributeType> headerAttributeTypes	=	new HashMap<String, AttributeType>();
		AttributeType idType		= new AttributeType("id", String.class, true);
		AttributeType timestampType	= new AttributeType("timestamp", Long.class, true);
		headerAttributeTypes.put(idType.getName(), idType);
		headerAttributeTypes.put(timestampType.getName(), timestampType);
		
		Map<String, AttributeType> applicationAttributeTypes =	new HashMap<String, AttributeType>();
		AttributeType consumerIdType 		= new AttributeType("consumerId", String.class, true);
		AttributeType providerIdType 		= new AttributeType("providerId", String.class, true);
		AttributeType operationNameType 	= new AttributeType("operationName", String.class, true);
		AttributeType parameterTypesType 	= new AttributeType("parameterTypes", Class[].class, true);
		AttributeType parameterValuesType	= new AttributeType("parameterValues", Object[].class, true);
		AttributeType returnTypeType		= new AttributeType("returnType", Class.class, true);
		AttributeType returnValueType		= new AttributeType("returnValue", Object.class, true);
		AttributeType successType			= new AttributeType("success", Boolean.class, true);
		AttributeType exceptionType			= new AttributeType("exception", Exception.class, false);
		AttributeType resquestTimestampType	= new AttributeType("resquestTimestamp", Long.class, true);
		AttributeType responseTimestampType	= new AttributeType("responseTimestamp", Long.class, true);
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
		
		EventType invocationEventType = new EventType("InvocationEvent", false, headerAttributeTypes, applicationAttributeTypes);
		System.out.println(invocationEventType.toString());
		System.out.println(invocationEventType.getHeaderAttributeTypeList());
		System.out.println(invocationEventType.getRequiredHeaderAttributeTypeList());
		System.out.println(invocationEventType.getOptionalHeaderAttributeTypeList());
		
		System.out.println(invocationEventType.getApplicationAttributeTypeList());
		System.out.println(invocationEventType.getRequiredApplicationAttributeTypeList());
		System.out.println(invocationEventType.getOptionalApplicationAttributeTypeList());
		
		
		Map<String, Attribute> headerAttributes	=	new HashMap<String, Attribute>();
		Attribute id		= new Attribute("123", idType);
		Attribute timestamp	= new Attribute(System.nanoTime(), timestampType);
		headerAttributes.put(idType.getName(), id);
		headerAttributes.put(timestampType.getName(), timestamp);
		
		Map<String, Attribute> applicationAttributes =	new HashMap<String, Attribute>();
		Attribute consumerId 		= new Attribute("consumer1", consumerIdType);
		Attribute providerId 		= new Attribute("provider1", providerIdType);
		Attribute operationName 	= new Attribute("operationName", operationNameType);
		Attribute parameterTypes 	= new Attribute(new Class[] {String.class}, parameterTypesType);
		Attribute parameterValues	= new Attribute(new Object[] {"parameterValues"}, parameterValuesType);
		Attribute returnType		= new Attribute(Long.class, returnTypeType);
		Attribute returnValue		= new Attribute(100, returnValueType);
		Attribute success			= new Attribute(true, successType);
		Attribute exception			= new Attribute(null, exceptionType);
		Attribute resquestTimestamp	= new Attribute(System.nanoTime(), resquestTimestampType);
		Attribute responseTimestamp	= new Attribute(System.nanoTime(), responseTimestampType);
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
