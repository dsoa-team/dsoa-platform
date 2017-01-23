package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.service.ProvidedPort;

public class ProvidedPortImpl extends PortImpl implements ProvidedPort {

	public ProvidedPortImpl(String name, ServiceSpecification serviceSpec) {
		super(name, serviceSpec);
	}

}
