package br.ufpe.cin.dsoa.api.event.agent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Filter {
	// <filter expression="event.data.exceptionClass" operator="EQ"
	// value="OutOfScheduleException"/>

	@XmlAttribute(name = "expression")
	private String expression;

	
	@XmlAttribute(name = "operator")
	private String operator;

	@XmlAttribute(name = "value")
	private String value;

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Filter [expression=" + expression + "]";
				/*"+ ", operator=" + operator + ", value=" + value
				+ "]";*/
	}

}
