package br.ufpe.cin.dsoa.api.attribute;



public class AttributeAlreadyCatalogedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6776265526239640428L;
	
	private static final String ERROR_MSG = "Attribute already cataloged: %s.%s";
	
	public AttributeAlreadyCatalogedException(Attribute attribute) {
		super(String.format(ERROR_MSG, attribute.getCategory(), attribute.getName()));
	}

}
