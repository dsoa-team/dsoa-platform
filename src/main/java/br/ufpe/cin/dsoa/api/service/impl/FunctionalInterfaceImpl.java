package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.service.FunctionalInterface;

public class FunctionalInterfaceImpl implements FunctionalInterface {

	private String interfaceName;
	
	public FunctionalInterfaceImpl(String interfaceName) {
		super();
		this.interfaceName = interfaceName;
	}
	
	@Override
	public String getInterfaceName() {
		return this.interfaceName;
	}

}
