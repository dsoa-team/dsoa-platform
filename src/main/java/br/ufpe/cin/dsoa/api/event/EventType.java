package br.ufpe.cin.dsoa.api.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import br.ufpe.cin.dsoa.util.Constants;

/**
 * 
 * Represent an event type (Type Object Pattern). Each new event type will be an
 * instance of this class which includes the attributes that characterize this
 * particular event type.
 * 
 * @author dsoa-team
 * 
 */
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class EventType {

	public static final String METADATA = "metadata";
	public static final String DATA = "data";
	public static final String PROPERTY = "property";

	@XmlAttribute(name = "type", required = true)
	private String name;

	// @XmlJavaTypeAdapter(EventTypeAdapter.class)
	private EventType superType = null;

	@XmlAttribute(name = "extends")
	private String superTypeName;

	private List<PropertyType> metadata;
	private Map<String, PropertyType> metadataMap = new HashMap<String, PropertyType>();

	private List<PropertyType> data;
	private Map<String, PropertyType> dataMap = new HashMap<String, PropertyType>();

	EventType() {
		this.metadata = new ArrayList<PropertyType>() {
			@Override
			public boolean add(PropertyType propType) {
				String typeName = propType.getTypeName();
				try {
					propType.setClazz(Class.forName(typeName));
					metadataMap.put(propType.getName(), propType);
					return super.add(propType);
				} catch (ClassNotFoundException cnfe) {
					return false;
				}
			}
		};

		this.data = new ArrayList<PropertyType>() {
			@Override
			public boolean add(PropertyType propType) {
				String typeName = propType.getTypeName();
				try {
					propType.setClazz(Class.forName(typeName));
					dataMap.put(propType.getName(), propType);
					return super.add(propType);
				} catch (ClassNotFoundException cnfe) {
					return false;
				}
			}
		};

	}

	@XmlElementWrapper(name = METADATA)
	@XmlElement(name = PROPERTY)
	private List<PropertyType> getMetadata() {
		return metadata;
	}

	@XmlElementWrapper(name = DATA)
	@XmlElement(name = PROPERTY)
	private List<PropertyType> getData() {
		return data;
	}

	public EventType(String name, List<PropertyType> metadata,
			List<PropertyType> data) {
		this.name = name;
		this.metadata = metadata;
		this.data = data;
		this.loadMap(metadata, metadataMap);
		this.loadMap(data, dataMap);
	}

	public String getName() {
		return name;
	}

	public EventType getSuperType() {
		return superType;
	}

	public String getSuperTypeName() {
		return superTypeName;
	}

	public Map<String, PropertyType> getMetadataMap() {
		return metadataMap;
	}

	public Map<String, PropertyType> getDataMap() {
		return dataMap;
	}

	public PropertyType getMetadataPropertyType(String name) {
		return this.metadataMap.get(name);
	}

	public PropertyType getDataPropertyType(String name) {
		return this.dataMap.get(name);
	}

	public List<PropertyType> getRequiredMetadataAttributeTypeList() {
		return getRequiredPropertyTypeList(getMetadataList());
	}

	public List<PropertyType> getRequiredDataAttributeTypeList() {
		return getRequiredPropertyTypeList(getDataList());
	}

	public List<PropertyType> getOptionalMetadataAttributeTypeList() {
		List<PropertyType> requiredList = getRequiredPropertyTypeList(getMetadataList());
		List<PropertyType> optionalList = getMetadataList();
		optionalList.removeAll(requiredList);
		return optionalList;
	}

	public List<PropertyType> getOptionalDataAttributeTypeList() {
		List<PropertyType> requiredList = getRequiredPropertyTypeList(getDataList());
		List<PropertyType> optionalList = getDataList();
		optionalList.removeAll(requiredList);
		return optionalList;
	}

	public List<PropertyType> getMetadataList() {
		return getPropertyTypeList(
				new ArrayList<PropertyType>(metadataMap.values()),
				superType == null ? null : new ArrayList<PropertyType>(
						superType.metadataMap.values()));
	}

	public List<PropertyType> getDataList() {
		return getPropertyTypeList(
				new ArrayList<PropertyType>(dataMap.values()),
				superType == null ? null : new ArrayList<PropertyType>(
						superType.dataMap.values()));
	}

	public final Map<String, Object> toDefinitionMap() {
		Map<String, Object> map = new HashMap<String, Object>();

		// metadata
		Map<String, Object> metadataDef = new HashMap<String, Object>();
		if (!metadataMap.isEmpty()) {
			for (PropertyType property : metadataMap.values()) {
				metadataDef.put(property.getName(), property.getType());
			}
		}

		// data
		Map<String, Object> dataDef = new HashMap<String, Object>();
		if (!dataMap.isEmpty()) {
			for (PropertyType property : dataMap.values()) {
				dataDef.put(property.getName(), property.getType());
			}
		}

		map.put(Constants.EVENT_TYPE, String.class);
		map.put(Constants.EVENT_METADATA, metadataDef);
		map.put(Constants.EVENT_DATA, dataDef);

		return map;
	}

	private List<PropertyType> getPropertyTypeList(
			List<PropertyType> propertyTypes, List<PropertyType> superPropTypes) {

		Set<PropertyType> propertyTypesSet = new HashSet<PropertyType>();

		// add super type attributes
		if (superPropTypes != null) {
			propertyTypesSet.addAll(superPropTypes);
		}

		// add
		if (propertyTypes != null) {
			propertyTypesSet.addAll(propertyTypes);
		}

		return new ArrayList<PropertyType>(propertyTypesSet);
	}

	private void loadMap(List<PropertyType> lista,
			Map<String, PropertyType> mapa) {

		Iterator<PropertyType> it = lista.iterator();
		while (it.hasNext()) {
			PropertyType property = it.next();
			mapa.put(property.getName(), property);
		}
	}

	private List<PropertyType> getRequiredPropertyTypeList(
			List<PropertyType> propertyTypes) {
		List<PropertyType> required = null;
		if (propertyTypes != null) {
			required = new ArrayList<PropertyType>();
			for (PropertyType attr : propertyTypes) {
				if (attr.isRequired()) {
					required.add(attr);
				}
			}
		}
		return required;
	}

	public Map<String, PropertyType> getPropertiesMap() {
		List<PropertyType> propertyList = new ArrayList<PropertyType>();

		propertyList.addAll(this.getMetadataList());
		propertyList.addAll(this.getDataList());

		Map<String, PropertyType> propMap = new HashMap<String, PropertyType>();

		for (PropertyType propertyType : propertyList) {
			propMap.put(propertyType.getName(), propertyType);
		}

		return propMap;
	}

	public Event createEvent(Map<String, Object> metadata,
			Map<String, Object> data) {
		Map<String, Property> metadataValue = this.loadValues(metadata, true);
		Map<String, Property> dataValue = this.loadValues(data, false);
		Event event = new Event(this, metadataValue, dataValue);
		return event;
	}

	private Map<String, Property> loadValues(Map<String, Object> valueMap,
			boolean isMetadata) {

		Map<String, Property> propertyMap = new HashMap<String, Property>();

		for (String key : valueMap.keySet()) {
			PropertyType propertyType = this.getPropertyType(key, isMetadata);

			if (null != propertyMap) {
				Object value = valueMap.get(key);
				Property property = propertyType.createProperty(value);
				propertyMap.put(key, property);
			}
		}
		return propertyMap;
	}

	@Override
	public String toString() {
		return "EventType [name=" + name + ", superTypeName=" + superTypeName
				+ "]";
	}

	/**
	 * returns the propertyType from data or metadata map
	 * 
	 * @param isMetadata
	 * @return
	 */
	private PropertyType getPropertyType(String propertyTypeName,
			boolean isMetadata) {

		PropertyType propertyType = null;

		if (isMetadata) {
			propertyType = this.getMetadataPropertyType(propertyTypeName);
		} else {
			propertyType = this.getDataPropertyType(propertyTypeName);
		}

		return propertyType;
	}
}
