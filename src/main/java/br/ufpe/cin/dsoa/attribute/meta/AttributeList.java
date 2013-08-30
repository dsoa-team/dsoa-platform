package br.ufpe.cin.dsoa.attribute.meta;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "attributes")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttributeList {

	public static final String CONFIG = "DSOA-INF/attribute.xml";
	
	@XmlElement(name = "attribute")
	private List<AttributeType> attributes;

	public List<AttributeType> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeType> attributes) {
		this.attributes = attributes;
	}
}
