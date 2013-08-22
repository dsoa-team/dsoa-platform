package br.ufpe.cin.dsoa.attribute;


public class AttributeAlreadyCatalogedException extends Exception {

	private static final String ERROR_MSG = "Attribute already cataloged: %s.%s";
	
	public AttributeAlreadyCatalogedException(Attribute attribute) {
		super(String.format(ERROR_MSG, attribute.getCategory(), attribute.getName()));
	}

}
