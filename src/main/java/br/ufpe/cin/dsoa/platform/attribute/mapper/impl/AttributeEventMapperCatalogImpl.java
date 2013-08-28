package br.ufpe.cin.dsoa.platform.attribute.mapper.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.attribute.mapper.AttributeEventMapperAlreadyCatalogedException;
import br.ufpe.cin.dsoa.platform.attribute.mapper.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.util.AttributeParser;

/**
 * This component maintains the mapper catalog. Each mapper relates a QoS attribute with the event
 * that represents variations on the attribute value. 
 *  
 */
public class AttributeEventMapperCatalogImpl implements AttributeEventMapperCatalog {
	
	private Map<String,AttributeEventMapper> attributeEventMapperMap = new HashMap<String,AttributeEventMapper>();
	
	public AttributeEventMapper getAttributeEventMapper(String id) {
		return this.attributeEventMapperMap.get(id);
	}

	public List<AttributeEventMapper> getAttributeEventMapperList() {
		return new ArrayList<AttributeEventMapper>(this.attributeEventMapperMap.values());
	}

	public synchronized void addAttributeEventMapper(AttributeEventMapper mapper) throws AttributeEventMapperAlreadyCatalogedException {
		String attId = AttributeParser.format(mapper.getCategory(), mapper.getName());
		if (this.attributeEventMapperMap.containsKey(attId)) {
			throw new AttributeEventMapperAlreadyCatalogedException(mapper);
		}
		this.attributeEventMapperMap.put(attId, mapper);
	}

}
