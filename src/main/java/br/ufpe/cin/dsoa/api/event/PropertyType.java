package br.ufpe.cin.dsoa.api.event;

public class PropertyType {
	private Class<?> type;
	private String name;
	private boolean required;
	private String expression;

	PropertyType() {}
	
	public PropertyType(String name, Class<?> type, String expression,
			boolean required) {
		super();
		this.name = name;
		this.type = type;
		this.expression = expression;
		this.required = required;
	}

	public PropertyType(String name, Class<?> type, boolean required) {
		super();
		this.type = type;
		this.name = name;
		this.required = required;
		this.expression = "";
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public boolean isRequired() {
		return required;
	}

	public String getExpression() {
		return expression;
	}

	@Override
	public String toString() {
		return "PropertyType [type=" + type + ", name=" + name + ", required="
				+ required + ", expression=" + expression + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyType other = (PropertyType) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (required != other.required)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
