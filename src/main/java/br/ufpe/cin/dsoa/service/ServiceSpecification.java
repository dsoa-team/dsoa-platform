package br.ufpe.cin.dsoa.service;

public interface ServiceSpecification {

	String[] getClassNames();
	
	Class<?>[] getClasses();
	
	NonFunctionalSpecification getNonFunctionalSpecification();

}