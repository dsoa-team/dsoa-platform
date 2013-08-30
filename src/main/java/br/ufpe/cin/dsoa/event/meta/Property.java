package br.ufpe.cin.dsoa.event.meta;

public class Property {
	private PropertyType propertyType;
	private Object value;
	
	public Property(Object value, PropertyType type) {
		this.value = value;
		this.propertyType = type;
		isValid();
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
				throw new IllegalArgumentException("Value " + value + " is not an instance of " + type);
			}
		}
	}

	@Override
	public String toString() {
		return "Property [propertyType=" + propertyType + ", value=" + value
				+ "]";
	}
	
	
}
