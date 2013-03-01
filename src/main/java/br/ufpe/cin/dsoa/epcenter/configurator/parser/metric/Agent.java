package br.ufpe.cin.dsoa.epcenter.configurator.parser.metric;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Agent {

	public static final String[] TYPES = {"mapping", "class", "query"};
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlElement(name = "query")
	private String query;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
