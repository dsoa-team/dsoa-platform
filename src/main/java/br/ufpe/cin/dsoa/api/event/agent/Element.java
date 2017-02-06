package br.ufpe.cin.dsoa.api.event.agent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Element {
	
	@XmlAttribute(name ="id") 
	private String id;
	
	public String getId() {
		return id;
	}
	
}
