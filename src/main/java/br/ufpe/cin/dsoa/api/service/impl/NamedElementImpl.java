package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.service.NamedElement;

public class NamedElementImpl implements NamedElement {

	private String name;
	
	public NamedElementImpl(String name) {
		this.name = name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
