package br.ufpe.cin.dsoa.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;


public class NonFunctionalSpecificationImpl implements NonFunctionalSpecification {
	
	private List<Constraint> attributeConstraints = new ArrayList<Constraint>();
	
	public NonFunctionalSpecificationImpl(List<Constraint> attributeConstraints) {
		super();
		this.attributeConstraints = new ArrayList<Constraint>(attributeConstraints);
	}

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.api.service.impl.NonFunctionalSpecification#getAttributeConstraints()
	 */
	@Override
	public List<Constraint> getConstraints() {
		return new ArrayList<Constraint>(attributeConstraints);
	}
}
