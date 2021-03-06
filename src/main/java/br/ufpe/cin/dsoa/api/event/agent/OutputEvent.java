package br.ufpe.cin.dsoa.api.event.agent;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import br.ufpe.cin.dsoa.api.event.PropertyType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutputEvent {
	
	private static final String METADATA = "metadata";

	private static final String PROPERTY = "property";

	private static final String DATA = "data";

	@XmlAttribute(name = "type")
	private String type;
	
	@XmlElementWrapper(name = METADATA)
	@XmlElement(name = PROPERTY)
	private List<PropertyType> metadata;
	
	@XmlElementWrapper(name = DATA)
	@XmlElement(name = PROPERTY)
	private List<PropertyType> data;
	
	public List<PropertyType> getMetadata() {
		return metadata;
	}

	public List<PropertyType> getData() {
		return data;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "OutputEvent [type=" + type + ", metadata=" + metadata + ", data=" + data + "]";
	}

	
}
