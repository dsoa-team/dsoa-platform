package br.ufpe.cin.dsoa.configurator.parser.attribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import br.ufpe.cin.dsoa.attribute.AttributeId;
import br.ufpe.cin.dsoa.attribute.mappers.AttributeEventMapper;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Attribute {

	public static final String ATTRIBUTE_PREFIX	= "attribute.";
	public static final String CATEGORY 		= "category";
	public static final String NAME 			= "name";
	public static final String DESCRIPTION 		= "description";
	public static final String AGENT 			= "agent";
	
	@XmlElement(name = CATEGORY)
	private String category;
	
	@XmlElement(name = NAME)
	private String name;
	
	@XmlElement(name = DESCRIPTION)
	private String description;
	
	@XmlElement(name = AGENT)
	private Agent agent;

	private AttributeId attributeId;
	
	private AttributeEventMapper attEventMap;
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public AttributeId getId() {
		if (this.attributeId == null) {
			this.attributeId = new AttributeId(getCategory(),getName());
		}
		return this.attributeId;
	}

	public String getQuery() {
		return agent.getQuery();
	}

	public AttributeEventMapper getAttributeEventMapper() {
		return this.attEventMap;
	}
	
	public String toString() {
		return getId().toString();
	}
}
