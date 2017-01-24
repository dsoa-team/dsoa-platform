package br.ufpe.cin.dsoa.api.service;

import br.ufpe.cin.dsoa.api.service.impl.ServiceSpecification;


public interface Port extends NamedElement {
	
	public void setName(String name);

	public String getName();

	public ServiceSpecification getServiceSpecification() ;
	
	
}
