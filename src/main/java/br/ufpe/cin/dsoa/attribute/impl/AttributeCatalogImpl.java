package br.ufpe.cin.dsoa.attribute.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.attribute.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.attribute.AttributeCategory;

/**
 * This component maintains the attribute catalog. 
 */
public class AttributeCatalogImpl implements AttributeCatalog {
	
	private Map<String,Attribute> attributeMap = new HashMap<String,Attribute>();
	private Map<String,AttributeCategory> attributeCategoryMap = new HashMap<String,AttributeCategory>();
	
	public Attribute getAttribute(String id) {
		return this.attributeMap.get(id);
	}

	public Collection<Attribute> getAttributes() {
		return this.attributeMap.values();
	}

	public synchronized void addAttribute(Attribute attribute) throws AttributeAlreadyCatalogedException {
		if (this.attributeMap.containsKey(attribute.getId())) {
			throw new AttributeAlreadyCatalogedException(attribute);
		}
		attribute.getCategory().addAttribute(attribute);
		this.attributeMap.put(attribute.getId(), attribute);
	}

	public AttributeCategory getCategory(String catId) {
		return attributeCategoryMap.get(catId);
	}

	public void addCategory(AttributeCategory cat) {
		attributeCategoryMap.put(cat.getId(), cat);
	}

}
