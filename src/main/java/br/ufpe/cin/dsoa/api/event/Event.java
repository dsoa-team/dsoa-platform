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

	public Event(EventType eventType, Map<String, Property> metadataProperties,
			Map<String, Property> dataProperties) {
		super();
		this.eventType = eventType;
		this.metadata = new HashMap<String, Property>(metadataProperties);
		this.data = new HashMap<String, Property>(dataProperties);
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

	public boolean isRemote() {
		Property remoteProperty = this.getMetadataProperty("remote");
		boolean remote = (Boolean) ((remoteProperty == null) ? false : remoteProperty.getValue());

		return remote;
	}

	public void setRemote() {
		PropertyType remote = this.getEventType().getMetadataPropertyType("remote");
		Property isRemote = remote.createProperty(true);
		this.metadata.put("remote", isRemote);
	}

	/**
	 * Represents a platform event as a map
	 * 
	 * @return
	 */
	public final Map<String, Object> toMap() {
		Map<String, Object> eventMap = new HashMap<String, Object>();

		if (!metadata.isEmpty()) {
			for (Property property : metadata.values()) {
				eventMap.put(property.getPropertyType().getFullname(), property.getValue());
			}
		}

		// data
		if (!data.isEmpty()) {
			for (Property property : data.values()) {
				eventMap.put(property.getPropertyType().getFullname(), property.getValue());
			}
		}

		eventMap.put(Constants.EVENT_TYPE, eventType.getName());

		return eventMap;
	}

	public List<Property> getMetadataProperties() {
		return getPropertyList(metadata);
	}

	public List<Property> getDataProperties() {
		return getPropertyList(data);
	}

	public Map<String, Property> getMetadata() {
		return this.metadata;
	}

	public Map<String, Property> getData() {
		return this.data;
	}

	public String get(String key) {
		return this.metadata.get(key).getValue().toString();
	}

	@Override
	public String toString() {
		return "Event [eventType=" + eventType.getName() + ", data=" + data + "]";
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

		if (!(requiredHeaderTypes.isEmpty() && requiredApplicationTypes.isEmpty())) {
			throw new IllegalArgumentException(
					"Invalid event! These attributes are required: Headers: " + requiredHeaderTypes
							+ " Application: " + requiredApplicationTypes);
		} else {
			if (!(headerAttributes.isEmpty() && aplicationAttributes.isEmpty())) {
				throw new IllegalArgumentException(
						"Invalid event! These attributes are not declared: Headers: "
								+ headerAttributes + " Application: " + aplicationAttributes);
			}
		}
	}

	private void validate(List<PropertyType> requiredAttributeTypes,
			List<PropertyType> optionalAttributeTypes, List<Property> attList) {
		Iterator<Property> attItr = null;
		if (attList != null) {
			attItr = attList.iterator();
			while (attItr.hasNext()) {
				Property property = attItr.next();
				if (requiredAttributeTypes.contains(property.getPropertyType())) {
					requiredAttributeTypes.remove(property.getPropertyType());
					attItr.remove();
				} else if (optionalAttributeTypes.contains(property.getPropertyType())) {
					optionalAttributeTypes.remove(property);
					attItr.remove();
				}
			}
		}
	}
}
