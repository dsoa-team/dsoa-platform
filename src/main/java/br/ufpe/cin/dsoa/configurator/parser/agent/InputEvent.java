package br.ufpe.cin.dsoa.configurator.parser.agent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class InputEvent {
	@XmlAttribute(name = "type")
	private String type;
	
	@XmlAttribute(name = "windowType")
	private String windowType;

	@XmlAttribute(name = "windowSize")
	private Long windowSize;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWindowType() {
		return windowType;
	}

	public void setWindowType(String windowType) {
		this.windowType = windowType;
	}

	public Long getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Long windowSize) {
		this.windowSize = windowSize;
	}
	
	
}
