package br.ufpe.cin.dsoa.api.event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import br.ufpe.cin.dsoa.util.Constants;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyType {
	
	public static final String ID 	= "id";
	public static final String TYPE 	= "type";
	
	@XmlAttribute(name = ID)
	private String name;
	
	@XmlAttribute(name = TYPE)
	private String typeName;
	
	@XmlAttribute(name = "generated")
	private boolean generated;
	
	// for agent expression definition
	@XmlAttribute(name = "expression")
	private String expression;
	
	private Class<?> type;
	
	@XmlAttribute(name = "required")
	private boolean required;
	
	private String namespace;
	
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
	
	public String getTypeName() {
		return typeName;
	}
	
	public Class<?> getType() {
		return type;
	}

	public void setClazz(Class<?> clazz) {
		this.type = clazz;
	}
	
	// generated event must be required (for validation)
	public boolean isRequired() {
		return required || generated;
	}

	public String getExpression() {
		return expression;
	}
	
	public boolean isGenerated() {
		return generated;
	}


	@Override
	public String toString() {
		return "PropertyType [name=" + name + ", typeName=" + typeName + ", generated=" + generated + ", expression="
				+ expression + ", type=" + type + ", required=" + required + ", namespace=" + namespace + "]";
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
	
	public Property createProperty(Object value){
		Property property = new Property(value, this);
		
		return property;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getNamespace() {
		return this.namespace;
	}
	

	public String getFullname() {
		return this.namespace + Constants.UNDERLINE + this.name;
	}
	
}
