package br.ufpe.cin.dsoa.event.meta;

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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import br.ufpe.cin.dsoa.event.meta.adapter.EventTypeAdapter;

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
@XmlAccessorType(XmlAccessType.FIELD)
public class EventType {

	@XmlAttribute(required = true)
	private String name;

	@XmlJavaTypeAdapter(EventTypeAdapter.class)
	private EventType superType = null;

	private List<PropertyType> metadata;

	private List<PropertyType> data;

	private Map<String, PropertyType> metadataMap;

	private Map<String, PropertyType> dataMap;

	public EventType(String name, List<PropertyType> metadata,
			List<PropertyType> data) {
		super();
		this.name = name;
		this.metadata = metadata;
		this.data = data;
		metadataMap = new HashMap<String, PropertyType>();
		dataMap = new HashMap<String, PropertyType>();
		this.loadMap(metadata, metadataMap);
		this.loadMap(data, dataMap);
	}

	public EventType(String name, List<PropertyType> metadata,
			List<PropertyType> data, EventType superType) {
		this(name, metadata, data);
		this.superType = superType;
	}

	public String getName() {
		return name;
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
		return getPropertyTypeList(metadata, superType == null ? null
				: superType.metadata);
	}

	public List<PropertyType> getDataList() {
		return getPropertyTypeList(data, superType == null ? null
				: superType.data);
	}
	
	
	public final Map<String, Object> toMap(){
		Map<String, Object> map = new HashMap<String, Object>();
		
		//metadata
		Map<String, Object> metadataDef = new HashMap<String, Object>();
		Iterator<PropertyType> metadata = this.metadata.iterator();
		while(metadata.hasNext()){
			PropertyType p = metadata.next();
			metadataDef.put(p.getName(), p.getType());
		}
		
		//data
		Map<String, Object> dataDef = new HashMap<String, Object>();
		Iterator<PropertyType> data = this.data.iterator();
		while(data.hasNext()){
			PropertyType p = data.next();
			dataDef.put(p.getName(), p.getType());
		}
		
		map.put("name", String.class);
		map.put("metadata", metadataDef);
		map.put("data", dataDef);
		
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

	private void loadMap(List<PropertyType> metadata,
			Map<String, PropertyType> metadataMap) {
		
		Iterator<PropertyType> it = metadata.iterator();
		while (it.hasNext()) {
			PropertyType property = it.next();
			metadataMap.put(property.getName(), property);
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
}
