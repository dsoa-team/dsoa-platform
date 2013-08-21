package br.ufpe.cin.dsoa.configurator.parser.attribute;

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

	public static final String CONFIG = "DSOA-INF/metric.xml";
	
	@XmlElement(name = "attribute")
	private List<Attribute> attributes;

	public List<Attribute> getMetrics() {
		return attributes;
	}

	public void setMetrics(List<Attribute> attributes) {
		this.attributes = attributes;
	}
}
