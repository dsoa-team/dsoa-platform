package br.ufpe.cin.dsoa.platform.attribute;

import java.util.Collection;

import br.ufpe.cin.dsoa.attribute.exception.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.attribute.meta.AttributeCategory;
import br.ufpe.cin.dsoa.attribute.meta.AttributeType;



public interface AttributeCatalog {

	public AttributeType getAttribute(String id);

	public Collection<AttributeType> getAttributes();

	public void addAttribute(AttributeType attribute) throws AttributeAlreadyCatalogedException;

	public AttributeCategory getCategory(String catId);

	public void addCategory(AttributeCategory cat);
	
}