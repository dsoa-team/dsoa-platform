package br.ufpe.cin.dsoa.api.event.agent;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;


@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class InputEvent {
	
	@XmlAttribute(name = "type")
	private String type;

	@XmlAttribute(name = "alias")
	private String alias;
	
	@XmlElement(name = "window")
	private Window window;
	
	@XmlElementWrapper(name = "filterExpression")
	@XmlElement(name ="filter") 
	private List<Filter> filter;

	public String getType() {
		return type;
	}

	public String getAlias() {
		return alias;
	}

	public Window getWindow() {
		return window;
	}
	
	public List<Filter> getFilter() {
		return filter;
	}

	@Override
	public String toString() {
		return "InputEvent [type=" + type + ", alias=" + alias + ", window=" + window + "]";
	}
	
	
	
}
