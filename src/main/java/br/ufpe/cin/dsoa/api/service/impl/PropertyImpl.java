package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.service.Property;

public class PropertyImpl extends NamedElementImpl implements Property  {

	private String type;
	private Object value;
	
	public PropertyImpl(String name, Object value, String type) {
		super(name);
		this.type = type;
		this.value = value;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

}
