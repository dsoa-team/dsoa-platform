package br.ufpe.cin.dsoa.attribute;

import java.util.Collection;

import br.ufpe.cin.dsoa.configurator.parser.attribute.Attribute;
import br.ufpe.cin.dsoa.configurator.parser.attribute.AttributeList;

public interface AttributeCatalog {

	public Attribute getAttribute(AttributeId id);

	public Collection<Attribute> getAttributes();

	public void addAttribute(Attribute attribute);

	public void addAttributes(AttributeList metrics);
	
}