package br.ufpe.cin.dsoa.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Event {
	private EventType eventType;
	private Map<String, Attribute<?>> headerAttributes;
	private Map<String, Attribute<?>> applicationAttributes;
	
	public Event(EventType eventType,
			Map<String, Attribute<?>> headerAttributes,
			Map<String, Attribute<?>> applicationAttributes) {
		super();
		this.eventType = eventType;
		this.headerAttributes = headerAttributes;
		this.applicationAttributes = applicationAttributes;
		validate();
	}

	public EventType getEventType() {
		return eventType;
	}

	public Attribute<?> getAttribute(String name) {
		Attribute<?> attribute = null;
		attribute = headerAttributes.get(name);
		if (attribute == null) {
			attribute = applicationAttributes.get(name);
		}
		return attribute;
	}
	
	private List<Attribute<?>> getAttributeList(Map<String, Attribute<?>> attributes) {
		List<Attribute<?>> attList = new ArrayList<Attribute<?>>(attributes.values());
		return attList;
	}
	
	public List<Attribute<?>> getHeaderAttributes() {
		return getAttributeList(headerAttributes);
	}
	
	public List<Attribute<?>> getApplicationAttributes() {
		return getAttributeList(applicationAttributes);
	}
	
	private void validate() {
		List<AttributeType<?>> headerTypes = eventType.getHeaderAttributeTypeList();
		List<AttributeType<?>> applicationTypes = eventType.getApplicationAttributeTypeList();
		
		List<Attribute<?>> headerAtts = this.getHeaderAttributes();
		List<Attribute<?>> attributes = this.getApplicationAttributes();
	}
	
	private boolean validateAttributes(List<AttributeType<?>> lstAttTypes, List<Attribute<?>> lstAtt) {
		boolean valid = true;
		for(Attribute<?> att : lstAtt) {
			if (!lstAttTypes.contains(att.getAttributeType())) {
				return false;
			} 
		}
		return valid;
	}
}	
