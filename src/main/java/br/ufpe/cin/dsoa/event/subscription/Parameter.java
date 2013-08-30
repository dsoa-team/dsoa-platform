package br.ufpe.cin.dsoa.event.subscription;

public class Parameter {

	private String name;
	private Class<?> type;
	private Object value;

	public Parameter(String name, Class<?> type, Object value) {
		super();
		if (!type.isInstance(value)) {
			throw new IllegalArgumentException("Value is not compatible with class: " + type.getName());
		}
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}
}
