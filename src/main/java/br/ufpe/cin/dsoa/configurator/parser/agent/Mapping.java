package br.ufpe.cin.dsoa.configurator.parser.agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import br.ufpe.cin.dsoa.configurator.parser.Property;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mapping {

	@XmlElement(name = "property")
	private List<Property> properties;

	public List<Property> getProperties() {
		return Collections.unmodifiableList(properties);
	}

}
