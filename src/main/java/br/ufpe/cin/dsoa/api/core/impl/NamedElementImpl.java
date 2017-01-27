package br.ufpe.cin.dsoa.api.core.impl;

import br.ufpe.cin.dsoa.api.core.NamedElement;

public class NamedElementImpl implements NamedElement {

	private String name;
	
	public NamedElementImpl(String name) {
		if (name == null) {
			throw new RuntimeException("A NamedElement cannot have a Null name!");
		}
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
