package br.ufpe.cin.dsoa.event.agent.meta;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Processing.class})
@XmlRootElement(name = "query")
public class ProcessingQuery extends Processing {

	@XmlAttribute(name = "command")
	private String query;

	public String getQuery() {
		return query;
	}

	@Override
	public String toString() {
		return "ProcessingQuery [ Id=" + getId() + ", query=" + query +"]";
	}


}
