package br.ufpe.cin.dsoa.event;

public class Attribute {
	private AttributeType attributeType;
	private Object value;
	
	public Attribute(Object value, AttributeType type) {
		this.value = value;
		this.attributeType = type;
		isValid();
	}

	public Object getValue() {
		return value;
	}
	
	public AttributeType getAttributeType() {
		return attributeType;
	}
	
	private void isValid() throws IllegalArgumentException {
		if (value != null) {
			Class<?> type = this.attributeType.getType();
			if (!type.isAssignableFrom(value.getClass())) {
				throw new IllegalArgumentException("Value " + value + " is not an instance of " + type);
			}
		}
	}

	@Override
	public String toString() {
		return "Attribute [attributeType=" + attributeType + ", value=" + value
				+ "]";
	}
	
	
}
