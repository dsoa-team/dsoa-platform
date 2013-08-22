package br.ufpe.cin.dsoa.mapper;

import java.util.List;

public interface AttributeEventMapperCatalog {
	public AttributeEventMapper getAttributeEventMapper(String id) ;

	public List<AttributeEventMapper> getAttributeEventMapperList();
	
	public void addAttributeEventMapper(AttributeEventMapper mapper) throws AttributeEventMapperAlreadyCatalogedException;
	
}
