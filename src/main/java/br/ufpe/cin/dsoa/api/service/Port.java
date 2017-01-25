package br.ufpe.cin.dsoa.api.service;



public interface Port extends NamedElement {
	
	public void setName(String name);

	public String getName();

	public ServiceSpecification getServiceSpecification() ;
	
	
}
