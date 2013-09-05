package br.ufpe.cin.dsoa.api.attribute.mapper;

import javax.xml.bind.annotation.XmlAttribute;

public class AttributeEventPropertyMapper {

	private static final String ID = "id";
	private static final String EXPRESSION = "expression";
	

	@XmlAttribute(name = ID)
	private String name;
	
	@XmlAttribute(name = EXPRESSION)
	private String expression;

	public String getId() {
		return name;
	}

	public String getExpression() {
		return expression;
	}
	
}
