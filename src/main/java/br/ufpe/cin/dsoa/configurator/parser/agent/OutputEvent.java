package br.ufpe.cin.dsoa.configurator.parser.agent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutputEvent {
	
	@XmlElement(name = "mapping")
	private Mapping mapping;
	
	@XmlAttribute(name = "type")
	private String type;

	
	
	public Mapping getMapping() {
		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
