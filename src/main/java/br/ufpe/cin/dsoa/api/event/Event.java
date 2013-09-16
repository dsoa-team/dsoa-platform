package br.ufpe.cin.dsoa.api.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.util.Constants;

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
	
	public Property getMetadataProperty(String name) {
		return metadata.get(name);
	}
	
	public Property getDataProperty(String name) {
		return data.get(name);
	}
	
	public final Map<String, Object> toMap() {
		Map<String, Object> eventMap = new HashMap<String, Object>();
		
		eventMap.put(Constants.EVENT_TYPE, eventType.getName());
		eventMap.put(Constants.EVENT_METADATA,this.buildPropertyMap(true));
		eventMap.put(Constants.EVENT_DATA,this.buildPropertyMap(false));
		
		return eventMap;
	}
	
	public List<Property> getMetadataProperties() {
		return getPropertyList(metadata);
	}
	
	public List<Property> getDataProperties() {
		return getPropertyList(data);
	}
	
	public Map<String, Property> getMetadata(){
		return this.metadata;
	}
	
	public Map<String, Property> getData(){
		return this.data;
	}
	
	public String get(String key){
		return this.metadata.get("key").getValue().toString();
	}
	
	
	@Override
	public String toString() {
		return "Event [eventType=" + eventType + ", metadata=" + metadata
				+ ", data=" + data + "]";
	}

	private Map<String, Object> buildPropertyMap(boolean isMetadata) {

		Map<String, Object> map = new HashMap<String, Object>();
		
		Map<String, Property> values;
		List<PropertyType> typeList;
		
		if(isMetadata){
			values = this.metadata;
			typeList = this.eventType.getMetadataList();
		} else {
			values = this.data;
			typeList = this.eventType.getDataList();
		}
		
		if(!typeList.isEmpty()){
			Iterator<PropertyType> it = typeList.iterator();
			while(it.hasNext()){
				PropertyType propertyType = it.next();
				map.put(propertyType.getName(), (values.get(propertyType.getName()) == null)? null : values.get(propertyType.getName()).getValue());
			}
		}
		
		return map;
	}
	private List<Property> getPropertyList(Map<String, Property> properties) {
		List<Property> attList = new ArrayList<Property>();
		if (properties != null) {
			attList.addAll(properties.values());
		}
		return attList;
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
				if (requiredAttributeTypes.contains(property.getPropertyType())) {
					requiredAttributeTypes.remove(property.getPropertyType());
					attItr.remove();
				} else if (optionalAttributeTypes.contains(property.getPropertyType())){
					optionalAttributeTypes.remove(property);
					attItr.remove();
				}
			}
		}
	}
}	
