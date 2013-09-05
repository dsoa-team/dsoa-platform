package br.ufpe.cin.dsoa.api.attribute;

import java.util.Map;

import br.ufpe.cin.dsoa.api.event.Property;


public class AttributeValue {
/*
	@XmlElementWrapper(name = METADATA)
	@XmlElement(name = PROPERTY)
	private List<AttributePropertyType> metadata;

	@XmlElementWrapper(name = DATA)
	@XmlElement(name = PROPERTY, required=true)
	private List<AttributePropertyType> data;*/
	
	private Attribute attribute;
	private Map<String, Property> metadata;
	private Map<String, Property> data;
}
