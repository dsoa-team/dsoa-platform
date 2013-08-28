package br.ufpe.cin.dsoa.platform.attribute;

import java.util.Collection;

import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.attribute.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.attribute.AttributeCategory;



public interface AttributeCatalog {

	public Attribute getAttribute(String id);

	public Collection<Attribute> getAttributes();

	public void addAttribute(Attribute attribute) throws AttributeAlreadyCatalogedException;

	public AttributeCategory getCategory(String catId);

	public void addCategory(AttributeCategory cat);
	
}