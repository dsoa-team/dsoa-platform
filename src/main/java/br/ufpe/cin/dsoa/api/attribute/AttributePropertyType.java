package br.ufpe.cin.dsoa.api.attribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttributePropertyType {
	
	public static final String ID 	= "id";
	public static final String TYPE 	= "type";
	
	@XmlAttribute(name = ID)
	private String id;
	
	@XmlAttribute(name = TYPE)
	private String type;
	
	private Class<?> clazz;
	
	public String getId() {
		return id;
	}
	public String getType() {
		return type;
	}

	public Class<?> getClazz() {
		return this.clazz;
	}
	
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	@Override
	public String toString() {
		return "AttributePropertyType [id=" + id + ", type=" + type + ", clazz=" + clazz + "]";
	}
	
	
}
