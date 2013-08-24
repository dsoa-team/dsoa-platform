package br.ufpe.cin.dsoa.agent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Window {
	//<window type="lenght" size="10" />
	
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlAttribute(name = "size")
	private int size;
	
	@XmlAttribute(name = "unit")
	private String unit;

	public String getType() {
		return type;
	}

	public int getSize() {
		return size;
	}

	public String getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		return "Window [type=" + type + ", size=" + size + ", unit=" + unit + "]";
	}
	
	
	
}
