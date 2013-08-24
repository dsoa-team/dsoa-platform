package br.ufpe.cin.dsoa.agent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventProcessingAgent {

	@XmlAttribute(name = "id")
	private String id;
	
	@XmlElement(name = "description")
	private String description;
	
	@XmlElementRefs({@XmlElementRef(type=br.ufpe.cin.dsoa.agent.ProcessingQuery.class),@XmlElementRef(type=br.ufpe.cin.dsoa.agent.ProcessingMapping.class)})
	private Processing processing;

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Processing getProcessing() {
		return processing;
	}

	@Override
	public String toString() {
		return "EventProcessingAgent [id=" + id + ", description=" + description + ", processing=" + processing + "]";
	}

	
}
