package br.ufpe.cin.dsoa.platform.attribute.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.attribute.exception.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.attribute.meta.AttributeCategory;
import br.ufpe.cin.dsoa.attribute.meta.AttributeType;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;

/**
 * This component implements DSOA Attributes Catalog. It maintains a register of
 * the QoS attributes that are supported by the platform.
 * 
 **/
public class AttributeCatalogImpl implements AttributeCatalog {
	
	private Map<String,AttributeType> attributeMap = new HashMap<String,AttributeType>();
	private Map<String,AttributeCategory> attributeCategoryMap = new HashMap<String,AttributeCategory>();
	
	public AttributeType getAttribute(String id) {
		return this.attributeMap.get(id);
	}

	public Collection<AttributeType> getAttributes() {
		return this.attributeMap.values();
	}

	public synchronized void addAttribute(AttributeType attribute) throws AttributeAlreadyCatalogedException {
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
