package br.ufpe.cin.dsoa.event.legacy;

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
	
	private Map<String, PropertyType> metadataAttributeTypes;
	
	private Map<String, PropertyType> dataAttributeTypes;
	
	private EventType superType = null;

	public EventType(String id, boolean composite,
			Map<String, PropertyType> headerAttributes,
			Map<String, PropertyType> applicationAttributes) {
		super();
		this.id = id;
		this.composite = composite;
		this.metadataAttributeTypes = headerAttributes;
		this.dataAttributeTypes = applicationAttributes;
	}
	
	public EventType(String id, boolean composite,
			Map<String, PropertyType> metadataAttributes,
			Map<String, PropertyType> dataAttributes, EventType superType) {
		this(id, composite, metadataAttributes, dataAttributes);
		this.superType = superType;
	}

	public String getId() {
		return id;
	}

	public boolean isComposite() {
		return composite;
	}
	
	private List<PropertyType> getAttributeTypeList(Map<String, PropertyType> propertyTypes, 
			Map<String, PropertyType> superAttrTypes) {
		Set<PropertyType> attributeTypesSet = new HashSet<PropertyType>();
		
		if (superAttrTypes != null) {
			attributeTypesSet.addAll(superAttrTypes.values());
		}
		
		if (propertyTypes != null) {
			attributeTypesSet.addAll(propertyTypes.values());
		}
		
		return new ArrayList<PropertyType>(attributeTypesSet);
	}
	
	private List<PropertyType> getRequiredAttributeTypeList(List<PropertyType> propertyTypes) {
		List<PropertyType> required = null;
		if (propertyTypes != null) {
			required = new ArrayList<PropertyType>();
			for(PropertyType attr : propertyTypes){
				if (attr.isRequired()) {
					required.add(attr);
				}
			}
		}
		return required;
	}
	
	public List<PropertyType> getRequiredMetadataAttributeTypeList() {
		return getRequiredAttributeTypeList(getMetadataAttributeTypeList());
	}
	
	public List<PropertyType> getRequiredDataAttributeTypeList() {
		return getRequiredAttributeTypeList(getDataAttributeTypeList());
	}
	
	public List<PropertyType> getOptionalMetadataAttributeTypeList() {
		List<PropertyType> requiredList = getRequiredAttributeTypeList(getMetadataAttributeTypeList());
		List<PropertyType> optionalList = getMetadataAttributeTypeList();
		optionalList.removeAll(requiredList);
		return optionalList;
	}
	
	public List<PropertyType> getOptionalDataAttributeTypeList() {
		List<PropertyType> requiredList = getRequiredAttributeTypeList(getDataAttributeTypeList());
		List<PropertyType> optionalList = getDataAttributeTypeList();
		optionalList.removeAll(requiredList);
		return optionalList;
	}
	
	public List<PropertyType> getMetadataAttributeTypeList() {
		return getAttributeTypeList(metadataAttributeTypes, superType == null ? null : superType.metadataAttributeTypes);
	}
	
	public List<PropertyType> getDataAttributeTypeList() {
		return getAttributeTypeList(dataAttributeTypes, superType == null ? null : superType.dataAttributeTypes);
	}
	
	@Override
	public String toString() {
		return "EventType [id=" + id + ", composite=" + composite
				+ ", metadataAttributeTypes=" + metadataAttributeTypes
				+ ", dataAttributeTypes=" + dataAttributeTypes
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
		Map<String, PropertyType> headerAttributeTypes	=	new HashMap<String, PropertyType>();
		PropertyType id		= new PropertyType("id", String.class, true);
		PropertyType timestamp	= new PropertyType("timestamp", Long.class, true);
		headerAttributeTypes.put(id.getName(), id);
		headerAttributeTypes.put(timestamp.getName(), timestamp);
		
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
		System.out.println(invocationEventType.getMetadataAttributeTypeList());
		System.out.println(invocationEventType.getRequiredMetadataAttributeTypeList());
		System.out.println(invocationEventType.getOptionalMetadataAttributeTypeList());
		
		System.out.println(invocationEventType.getDataAttributeTypeList());
		System.out.println(invocationEventType.getRequiredDataAttributeTypeList());
		System.out.println(invocationEventType.getOptionalDataAttributeTypeList());
		
		
		Map<String, PropertyType> hdrAttributeTypes	=	new HashMap<String, PropertyType>();
		PropertyType idWs		= new PropertyType("id", Long.class, true);
		hdrAttributeTypes.put(idWs.getName(), idWs);
		Map<String, PropertyType> appAttributeTypes =	new HashMap<String, PropertyType>();
		PropertyType addressType 		= new PropertyType("addressType", String.class, true);
		appAttributeTypes.put(addressType.getName(), addressType);
		
		EventType invocationWSEventType 	= new EventType("InvocationEvent", false, hdrAttributeTypes, appAttributeTypes, invocationEventType);
		System.out.println(invocationWSEventType.toString());
		System.out.println(invocationWSEventType.getMetadataAttributeTypeList());
		System.out.println(invocationWSEventType.getRequiredMetadataAttributeTypeList());
		System.out.println(invocationWSEventType.getOptionalMetadataAttributeTypeList());
		System.out.println(invocationWSEventType.getDataAttributeTypeList());
		System.out.println(invocationWSEventType.getRequiredDataAttributeTypeList());
		System.out.println(invocationWSEventType.getOptionalDataAttributeTypeList());
	}
}
