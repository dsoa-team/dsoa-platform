package br.ufpe.cin.dsoa.attribute;

import java.util.Collection;



public interface AttributeCatalog {

	public Attribute getAttribute(String id);

	public Collection<Attribute> getAttributes();

	public void addAttribute(Attribute attribute) throws AttributeAlreadyCatalogedException;

	public AttributeCategory getCategory(String catId);

	public void addCategory(AttributeCategory cat);
	
}