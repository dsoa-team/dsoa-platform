package br.ufpe.cin.dsoa.api.event.agent;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Context {
	
	@XmlAttribute(name = "id")
	private String id;
	
	@XmlElement(name="element")
	private List<Element> elements;

	public String getId() {
		return id;
	}

	public List<Element> getElements() {
		return elements;
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("[Context id=" + id);
		for (Element element : elements) {
			buff.append("[Element id=" + element.getId()+"]");
		}
		buff.append("]");
		return buff.toString();
	}
}
