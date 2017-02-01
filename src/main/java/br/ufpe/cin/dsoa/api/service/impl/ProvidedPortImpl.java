package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.service.ProvidedPort;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;

public class ProvidedPortImpl extends PortImpl implements ProvidedPort {

	public ProvidedPortImpl(String name, ServiceSpecification serviceSpec) {
		super(name, serviceSpec);
	}

}
