package br.ufpe.cin.dsoa.api.attribute.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventType;
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
	private String eventTypeName;
	
	@XmlAttribute(name = EVENT_ALIAS)
	private String eventAlias;
	
	private Attribute attribute;
	
	private EventType eventType;
	
	@XmlElementWrapper(name = METADATA)
	@XmlElement(name = PROPERTY)
	private List<AttributeEventPropertyMapper> metadata;
	
	@XmlElementWrapper(name = DATA)
	@XmlElement(name = PROPERTY)
	private List<AttributeEventPropertyMapper> data;

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	public EventType getEventType() {
		return this.eventType;
	}

	public String getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public String getEventTypeName() {
		return eventTypeName;
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
	
	void afterUnmarshal( Unmarshaller u, Object parent )
    {
        System.out.println( "After unmarshal: " + parent.getClass() );
    }
	
	public AttributeValue convertToAttribute(Event event) {
		
		Map<String, Property> attMetadata = new HashMap<String, Property>();
		Map<String, Property> attData = new HashMap<String, Property>();
		
		for (AttributeEventPropertyMapper propMap : metadata) {
			String exp = propMap.getExpression();
			exp = exp.replaceFirst(this.eventAlias + ".", "").replaceFirst("metadata.", "");
			Property propertyValue = event.getMetadataProperty(exp);
			attMetadata.put(propertyValue.getPropertyType().getName(), propertyValue);
		}
		
		for (AttributeEventPropertyMapper propMap : data) {
			String exp = propMap.getExpression();
			exp = exp.replaceFirst(this.eventAlias + ".", "").replaceFirst("data.", "");
			Property propertyValue = event.getDataProperty(exp);
			attData.put(propertyValue.getPropertyType().getName(), propertyValue);
		}
		
		return new AttributeValue(this.attribute, attMetadata, attData);
	}
}
