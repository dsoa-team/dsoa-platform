package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.service.Port;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;

public class PortImpl implements Port {

	private String name;
	private ServiceSpecification serviceSpec;
	
	public PortImpl(String name, ServiceSpecification serviceSpec) {
		this.name = name;
		this.serviceSpec = serviceSpec;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ServiceSpecification getServiceSpecification() {
		return this.serviceSpec;
	}

}
