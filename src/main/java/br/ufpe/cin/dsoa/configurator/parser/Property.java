package br.ufpe.cin.dsoa.configurator.parser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {

	@XmlAttribute(required = true)
	private String id;
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlAttribute(name = "generated")
	private boolean generated;
	
	@XmlAttribute(name = "statistic")
	private String statistic;
	
	//for agent expression definition
	@XmlValue
	private String expression;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isGenerated() {
		return generated;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getStatistic() {
		return statistic;
	}

	public void setStatistic(String statistic) {
		this.statistic = statistic;
	}
	
	
}
