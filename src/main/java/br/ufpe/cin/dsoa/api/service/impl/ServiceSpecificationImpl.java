package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.service.FunctionalInterface;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;



/**
 * Represents the service specification, including functional and non-functional aspects. Functional aspects are
 * represented by the corresponding serviceInterface, while non-functional aspects are represented by a NonFunctionalSpecification
 * which is basically composed by a list of attribute constraints.
 * 
 * @author fabions
 *
 */
public class ServiceSpecificationImpl implements ServiceSpecification {
	
	private NonFunctionalSpecification nonFunctionalSpecification;
	private FunctionalInterface functionalInterface;
	
	
	public ServiceSpecificationImpl(String itfName,
			NonFunctionalSpecification nonFunctionalSpecification) {
		super();
		this.functionalInterface = new FunctionalInterfaceImpl(itfName);
		this.nonFunctionalSpecification = nonFunctionalSpecification;
	}

	
	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.api.service.impl.ServiceSpecification#getNonFunctionalSpecification()
	 */
	@Override
	public NonFunctionalSpecification getNonFunctionalSpecification() {
		return nonFunctionalSpecification;
	}

	@Override
	public FunctionalInterface getFunctionalInterface() {
		return functionalInterface;
	}

	public String getServiceInterface() {
		return this.getFunctionalInterface().getInterfaceName();
	}
}
