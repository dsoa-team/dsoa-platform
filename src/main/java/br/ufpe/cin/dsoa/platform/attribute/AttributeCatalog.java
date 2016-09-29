package br.ufpe.cin.dsoa.platform.attribute;

import java.util.Collection;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.attribute.AttributeCategory;



public interface AttributeCatalog {

	public Attribute getAttribute(String id);

	public Attribute getAttribute(String attCategory, String attName);

	public Collection<Attribute> getAttributes();

	public void addAttribute(Attribute attribute) throws AttributeAlreadyCatalogedException;
	
	public void removeAttribute(Attribute attribute);

	public AttributeCategory getCategory(String catId);

	public void addCategory(AttributeCategory cat);

	
}