package br.ufpe.cin.dsoa.event.legancy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Represent an event type (Type Object Pattern). Each new event type will be an instance of this class which
 * includes the attributes that characterize this particular event type.
 */
public class EventType {
	
	private String id;
	
	private boolean composite;
	
	private Map<String, AttributeType> headerAttributeTypes;
	
	private Map<String, AttributeType> applicationAttributeTypes;
	
	private EventType superType = null;

	public EventType(String id, boolean composite,
			Map<String, AttributeType> headerAttributes,
			Map<String, AttributeType> applicationAttributes) {
		super();
		this.id = id;
		this.composite = composite;
		this.headerAttributeTypes = headerAttributes;
		this.applicationAttributeTypes = applicationAttributes;
	}
	
	public EventType(String id, boolean composite,
			Map<String, AttributeType> headerAttributes,
			Map<String, AttributeType> applicationAttributes, EventType superType) {
		this(id, composite, headerAttributes, applicationAttributes);
		this.superType = superType;
	}

	public String getId() {
		return id;
	}

	public boolean isComposite() {
		return composite;
	}
	
	private List<AttributeType> getAttributeTypeList(Map<String, AttributeType> attributeTypes, 
			Map<String, AttributeType> superAttrTypes) {
		Set<AttributeType> attributeTypesSet = new HashSet<AttributeType>();
		
		if (superAttrTypes != null) {
			attributeTypesSet.addAll(superAttrTypes.values());
		}
		
		if (attributeTypes != null) {
			attributeTypesSet.addAll(attributeTypes.values());
		}
		
		return new ArrayList<AttributeType>(attributeTypesSet);
	}
	
	private List<AttributeType> getRequiredAttributeTypeList(List<AttributeType> attributeTypes) {
		List<AttributeType> required = null;
		if (attributeTypes != null) {
			required = new ArrayList<AttributeType>();
			for(AttributeType attr : attributeTypes){
				if (attr.isRequired()) {
					required.add(attr);
				}
			}
		}
		return required;
	}
	
	public List<AttributeType> getRequiredHeaderAttributeTypeList() {
		return getRequiredAttributeTypeList(getHeaderAttributeTypeList());
	}
	
	public List<AttributeType> getRequiredApplicationAttributeTypeList() {
		return getRequiredAttributeTypeList(getApplicationAttributeTypeList());
	}
	
	public List<AttributeType> getOptionalHeaderAttributeTypeList() {
		List<AttributeType> requiredList = getRequiredAttributeTypeList(getHeaderAttributeTypeList());
		List<AttributeType> optionalList = getHeaderAttributeTypeList();
		optionalList.removeAll(requiredList);
		return optionalList;
	}
	
	public List<AttributeType> getOptionalApplicationAttributeTypeList() {
		List<AttributeType> requiredList = getRequiredAttributeTypeList(getApplicationAttributeTypeList());
		List<AttributeType> optionalList = getApplicationAttributeTypeList();
		optionalList.removeAll(requiredList);
		return optionalList;
	}
	
	public List<AttributeType> getHeaderAttributeTypeList() {
		return getAttributeTypeList(headerAttributeTypes, superType == null ? null : superType.headerAttributeTypes);
	}
	
	public List<AttributeType> getApplicationAttributeTypeList() {
		return getAttributeTypeList(applicationAttributeTypes, superType == null ? null : superType.applicationAttributeTypes);
	}
	
	@Override
	public String toString() {
		return "EventType [id=" + id + ", composite=" + composite
				+ ", headerAttributeTypes=" + headerAttributeTypes
				+ ", applicationAttributeTypes=" + applicationAttributeTypes
				+ ", superType=" + superType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventType other = (EventType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public static void main(String[] args) {
		Map<String, AttributeType> headerAttributeTypes	=	new HashMap<String, AttributeType>();
		AttributeType id		= new AttributeType("id", String.class, true);
		AttributeType timestamp	= new AttributeType("timestamp", Long.class, true);
		headerAttributeTypes.put(id.getName(), id);
		headerAttributeTypes.put(timestamp.getName(), timestamp);
		
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
		applicationAttributeTypes.put(consumerIdType.getName(), consumerIdType);
		applicationAttributeTypes.put(providerIdType.getName(), providerIdType);
		applicationAttributeTypes.put(operationNameType.getName(), operationNameType);
		applicationAttributeTypes.put(consumerIdType.getName(), consumerIdType);
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
		
		
		Map<String, AttributeType> hdrAttributeTypes	=	new HashMap<String, AttributeType>();
		AttributeType idWs		= new AttributeType("id", Long.class, true);
		hdrAttributeTypes.put(idWs.getName(), idWs);
		Map<String, AttributeType> appAttributeTypes =	new HashMap<String, AttributeType>();
		AttributeType addressType 		= new AttributeType("addressType", String.class, true);
		appAttributeTypes.put(addressType.getName(), addressType);
		
		EventType invocationWSEventType 	= new EventType("InvocationEvent", false, hdrAttributeTypes, appAttributeTypes, invocationEventType);
		System.out.println(invocationWSEventType.toString());
		System.out.println(invocationWSEventType.getHeaderAttributeTypeList());
		System.out.println(invocationWSEventType.getRequiredHeaderAttributeTypeList());
		System.out.println(invocationWSEventType.getOptionalHeaderAttributeTypeList());
		System.out.println(invocationWSEventType.getApplicationAttributeTypeList());
		System.out.println(invocationWSEventType.getRequiredApplicationAttributeTypeList());
		System.out.println(invocationWSEventType.getOptionalApplicationAttributeTypeList());
	}
}
