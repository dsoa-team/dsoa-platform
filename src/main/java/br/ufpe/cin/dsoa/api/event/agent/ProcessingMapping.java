package br.ufpe.cin.dsoa.api.event.agent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Processing.class})
@XmlRootElement(name = "mapping")
public class ProcessingMapping extends Processing {

	@XmlElement(name = "input-event")
	private InputEvent inputEvent;
	
	@XmlElement(name = "output-event")
	private OutputEvent outputEvent;

	public InputEvent getInputEvent() {
		return inputEvent;
	}

	public OutputEvent getOutputEvent() {
		return outputEvent;
	}

	@Override
	public String toString() {
		return "ProcessingMapping [id=" + getId() + ", inputEvent=" + inputEvent + ", outputEvent=" + outputEvent + "]";
	}

	
}
