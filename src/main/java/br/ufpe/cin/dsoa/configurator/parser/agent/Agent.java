package br.ufpe.cin.dsoa.configurator.parser.agent;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Agent {

	@XmlAttribute(name = "id")
	private String id;
	
	@XmlElement(name = "description")
	private String description;
	
	@XmlElement(name = "transformer")
	private Transformer transformer;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Transformer getTransformer() {
		return transformer;
	}

	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	} 
	
	public List<String> getQueries() {
		return this.transformer.getQueries();
	}
}
