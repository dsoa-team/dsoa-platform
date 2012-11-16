package br.ufpe.cin.dsoa.event;

public class AttributeType {
	private Class<?> type;
	private String name;
	private boolean required;
	
	public AttributeType(String name, Class<?> type, boolean required) {
		super();
		this.name = name;
		this.type = type;
		this.required = required;
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

	@Override
	public String toString() {
		return "AttributeType [type=" + type + ", name=" + name + ", required="
				+ required + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AttributeType other = (AttributeType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
