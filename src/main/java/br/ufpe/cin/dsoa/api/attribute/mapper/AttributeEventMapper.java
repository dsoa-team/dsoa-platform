package br.ufpe.cin.dsoa.api.attribute.mapper;

import info.dmtree.notification.AlertItem;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.Property;

public class AttributeEventMapper {

	private static final String ATTRIBUTE_CATEGORY 	= "attribute-category";
	private static final String ATTRIBUTE_NAME	 	= "attribute-name";
	private static final String EVENT_TYPE 			= "event-type";
	private static final String EVENT_ALIAS 		= "event-alias";
	private static final String METADATA 			= "metadata";
	private static final String DATA 				= "data";
	private static final String PROPERTY			= "property";

	@XmlAttribute(name = ATTRIBUTE_CATEGORY)
	private String category;
	
	@XmlAttribute(name = ATTRIBUTE_NAME)
	private String name;
	
	@XmlAttribute(name = EVENT_TYPE)
	private String eventType;
	
	@XmlAttribute(name = EVENT_ALIAS)
	private String eventAlias;
	
	@XmlElementWrapper(name = METADATA)
	@XmlElement(name = PROPERTY)
	private List<AttributeEventPropertyMapper> metadata;
	
	@XmlElementWrapper(name = DATA)
	@XmlElement(name = PROPERTY)
	private List<AttributeEventPropertyMapper> data;

	public String getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public String getEventType() {
		return eventType;
	}

	public String getEventAlias() {
		return eventAlias;
	}

	public List<AttributeEventPropertyMapper> getMetadata() {
		return metadata;
	}

	public List<AttributeEventPropertyMapper> getData() {
		return data;
	}
	
	public AttributeValue convertToAttribute(Event event) {
		
		AttributeValue attributeValue = new AttributeValue();
		
		for (AttributeEventPropertyMapper propMap : metadata) {
			String exp = propMap.getExpression();
			exp = exp.replaceFirst(this.eventAlias + ".", "").replaceFirst("metadata.", "");
			
			Property propertyValue = event.getMetadataProperty(exp);
		}
		//event.get(key)
		
		for (AttributeEventPropertyMapper propMap : data) {
			String exp = propMap.getExpression();
			exp = exp.replaceFirst(this.eventAlias + ".", "").replaceFirst("data.", "");
		}
		
		return null;
	}
}
