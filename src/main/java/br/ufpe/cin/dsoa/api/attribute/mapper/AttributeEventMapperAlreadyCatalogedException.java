package br.ufpe.cin.dsoa.api.attribute.mapper;


public class AttributeEventMapperAlreadyCatalogedException extends Exception {
	
	private static final long serialVersionUID = 1204929221493454087L;
	
	private static final String ERROR_MSG = "A mapper was already cataloged for attribute: %s.%s";
	
	public AttributeEventMapperAlreadyCatalogedException(AttributeEventMapper mapper) {
		super(String.format(ERROR_MSG, mapper.getCategory(), mapper.getName()));
	}
}
