package br.ufpe.cin.dsoa.epcenter.configurator.parser.metric;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import br.ufpe.cin.dsoa.DsoaConstants;
import br.ufpe.cin.dsoa.management.MetricId;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Metric {

	public static final String METRIC_PREFIX	= "metric.";
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

	public MetricId getId() {
		return new MetricId(getCategory(),getName());
	}

	public String getQuery() {
		return agent.getQuery();
	}

	public String toString() {
		return this.getCategory() + DsoaConstants.TOKEN + this.getName();
	}
}
