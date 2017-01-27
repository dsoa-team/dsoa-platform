package br.ufpe.cin.dsoa.api.qos;

import java.util.List;

import br.ufpe.cin.dsoa.api.core.NamedElement;

public interface Category extends NamedElement {
	public List<Attribute> getAttributes();
	public Attribute getAttribute(String attName);
}
