package br.ufpe.cin.dsoa.api.service;


public class ServiceSpecification {
	
	private Class<?> clazz;
	private String serviceInterface;
	private NonFunctionalSpecification nonFunctionalSpecification;
	
	
	public ServiceSpecification(Class<?> clazz, String serviceInterface,
			NonFunctionalSpecification nonFunctionalSpecification) {
		super();
		this.clazz = clazz;
		this.serviceInterface = serviceInterface;
		this.nonFunctionalSpecification = nonFunctionalSpecification;
	}

	
	public NonFunctionalSpecification getNonFunctionalSpecification() {
		return nonFunctionalSpecification;
	}


	public Class<?> getClazz() {
		return clazz;
	}


	public String getServiceInterface() {
		return serviceInterface;
	}
}
