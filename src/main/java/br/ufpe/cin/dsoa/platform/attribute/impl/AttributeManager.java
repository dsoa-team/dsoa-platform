package br.ufpe.cin.dsoa.platform.attribute.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.attribute.AttributeCategory;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapperAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;

/**
 * This component implements DSOA Attributes Catalog. It maintains a register of
 * the QoS attributes that are supported by the platform.
 * 
 **/
public class AttributeManager implements AttributeCatalog, AttributeEventMapperCatalog {
	
	private Map<String,Attribute> attributeMap = new HashMap<String,Attribute>();
	private Map<String,AttributeCategory> attributeCategoryMap = new HashMap<String,AttributeCategory>();
	private Map<String,AttributeEventMapper> attributeEventMapperMap = new HashMap<String,AttributeEventMapper>();
	
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

	public AttributeEventMapper getAttributeEventMapper(String id) {
		return this.attributeEventMapperMap.get(id);
	}

	public List<AttributeEventMapper> getAttributeEventMapperList() {
		return new ArrayList<AttributeEventMapper>(this.attributeEventMapperMap.values());
	}

	public synchronized void addAttributeEventMapper(AttributeEventMapper mapper) throws AttributeEventMapperAlreadyCatalogedException {
		String attId = AttributeConstraint.format(mapper.getCategory(), mapper.getName());
		if (this.attributeEventMapperMap.containsKey(attId)) {
			throw new AttributeEventMapperAlreadyCatalogedException(mapper);
		}
		this.attributeEventMapperMap.put(attId, mapper);
	}
}
