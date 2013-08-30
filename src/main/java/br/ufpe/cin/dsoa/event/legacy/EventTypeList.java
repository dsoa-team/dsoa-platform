package br.ufpe.cin.dsoa.event.legacy;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "events")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventTypeList {

	private static final String CONFIG = "DSOA-INF/event.xml";
	
	@XmlElement(name = "event")
	private List<EventType> events;
}
