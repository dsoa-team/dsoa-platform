package br.ufpe.cin.dsoa.mapper.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.attribute.AttributeParser;
import br.ufpe.cin.dsoa.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.mapper.AttributeEventMapperAlreadyCatalogedException;
import br.ufpe.cin.dsoa.mapper.AttributeEventMapperCatalog;

/**
 * This component maintains the attribute catalog. 
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
