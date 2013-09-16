package br.ufpe.cin.dsoa.platform.attribute;

import java.util.List;

import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapperAlreadyCatalogedException;

public interface AttributeEventMapperCatalog {
	
	public AttributeEventMapper getAttributeEventMapper(String id) ;

	public List<AttributeEventMapper> getAttributeEventMapperList();
	
	public void addAttributeEventMapper(AttributeEventMapper mapper) throws AttributeEventMapperAlreadyCatalogedException;
	
}
