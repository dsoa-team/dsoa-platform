package br.ufpe.cin.dsoa.mapper;


public class AttributeEventMapperAlreadyCatalogedException extends Exception {
	private static final String ERROR_MSG = "A mapper was already cataloged for attribute: %s.%s";
	
	public AttributeEventMapperAlreadyCatalogedException(AttributeEventMapper mapper) {
		super(String.format(ERROR_MSG, mapper.getCategory(), mapper.getName()));
	}
}
