package br.ufpe.cin.dsoa.event.agent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Processing {

	@XmlAttribute(name = "id")
	private String id;

	public String getId() {
		return id;
	}
	
}
