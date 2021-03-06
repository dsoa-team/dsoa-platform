package br.ufpe.cin.dsoa.api.attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.platform.attribute.impl.AttributeCategoryAdapter;
import br.ufpe.cin.dsoa.util.Constants;


@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Attribute  {

	// constraint.operation.qos.performance.avgResponseTime.getCotation.LT = 500
	// constraint.service.qos.availability.LT = 99
	public static final String SERVICE_CONSTRAINT	= "constraint.service";
	public static final String OPERATION_CONSTRAINT	= "constraint.operation";
	public static final String CATEGORY 			= "category";
	public static final String NAME 				= "name";
	public static final String DESCRIPTION 			= "description";
	public static final String METADATA 			= "metadata";
	public static final String DATA 				= "data";
	public static final String PROPERTY 			= "property";
	
	private String id;
	
	@XmlAttribute(name = CATEGORY, required=true)
    @XmlJavaTypeAdapter(AttributeCategoryAdapter.class)
	private AttributeCategory category;
	
	@XmlAttribute(name = NAME, required=true)
	private String name;
	
	@XmlAttribute(name = DESCRIPTION)
	private String description;
	
	@XmlElementWrapper(name = METADATA)
	@XmlElement(name = PROPERTY)
	private List<PropertyType> metadataList;
	
	private Map<String, PropertyType> metadata = new HashMap<String, PropertyType>();

	@XmlElementWrapper(name = DATA)
	@XmlElement(name = PROPERTY, required=true)
	private List<PropertyType> dataList;
	
	private Map<String, PropertyType> data = new HashMap<String, PropertyType>();
	
	public String getId() {
		if (id == null) {
			id = category.getId() + Constants.TOKEN + this.name;
		}
		return id;
	}
	
	public AttributeCategory getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, PropertyType> getMetadata() {
		return metadata;
	}
	
	public List<PropertyType> getMetadataList() {
		return metadataList;
	}
	
	public void addMetadata(PropertyType propertyType) {
		this.metadata.put(propertyType.getName(), propertyType);
	}
	
	public Map<String, PropertyType> getData() {
		return data;
	}

	public List<PropertyType> getDataList() {
		return dataList;
	}
	
	public void addData(PropertyType propertyType) {
		this.data.put(propertyType.getName(), propertyType);
	}
	

	@Override
	public String toString() {
		return "Attribute [id=" + id + ", description=" + description + ", metadataList=" + metadataList + ", dataList=" + dataList
				+ "]";
	}


	
}
