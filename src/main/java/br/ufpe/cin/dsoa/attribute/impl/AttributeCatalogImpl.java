package br.ufpe.cin.dsoa.attribute.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.attribute.AttributeId;
import br.ufpe.cin.dsoa.configurator.parser.attribute.Attribute;
import br.ufpe.cin.dsoa.configurator.parser.attribute.AttributeList;

/**
 * This component maintains the attribute catalog. 
 */
public class AttributeCatalogImpl implements AttributeCatalog {
	
	private Map<AttributeId,Attribute> attributeMap = new HashMap<AttributeId,Attribute>();
	
	public Attribute getAttribute(AttributeId id) {
		return this.attributeMap.get(id);
	}

	public Collection<Attribute> getAttributes() {
		return this.attributeMap.values();
	}

	public void addAttribute(Attribute attribute) {
		this.attributeMap.put(attribute.getId(), attribute);
	}

	public void addAttributes(AttributeList metrics) {
		for (Attribute attribute : metrics.getMetrics()) {
			this.addAttribute(attribute);
		}
	}
	
}
