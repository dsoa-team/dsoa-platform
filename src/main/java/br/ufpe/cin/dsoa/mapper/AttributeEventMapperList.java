package br.ufpe.cin.dsoa.mapper;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "attribute-event-mappers")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttributeEventMapperList {
	
	public static final String CONFIG = "DSOA-INF/attribute-event-mapper.xml";;
	@XmlElement(name = "attribute-event-mapper")
	private List<AttributeEventMapper> attributeEventMappers;

	public List<AttributeEventMapper> getAttributesEventMappers() {
		return attributeEventMappers;
	}
}
