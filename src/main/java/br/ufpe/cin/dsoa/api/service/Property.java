package br.ufpe.cin.dsoa.api.service;

import br.ufpe.cin.dsoa.api.core.NamedElement;

public interface Property extends NamedElement {
	String getType();
	void setType(String type);
	
	Object getValue();
	void setValue(Object value);
}
