package br.ufpe.cin.dsoa.api.event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {
	private PropertyType propertyType;
	private Object value;

	@XmlAttribute(name = "expression")
	private String expression;

	public Property() {
	}

	public Property(Object value, PropertyType type) {
		this.value = value;
		this.propertyType = type;
		isValid();
	}

	public String getExpression() {
		return this.expression;
	}

	public Object getValue() {
		return value;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	private void isValid() throws IllegalArgumentException {
		if (value != null) {
			Class<?> type = this.propertyType.getType();
			if (!type.isAssignableFrom(value.getClass())) {
				//XXX: try cast values to their default event types
				try {
					value = propertyType.getType().cast(value);
				} catch (ClassCastException e) {
					throw new IllegalArgumentException("Value " + value + " is not an instance of "
							+ type);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Property [propertyType=" + propertyType.getName() + ", value=" + value
				+ ", expression=" + expression + "]";
	}

}
