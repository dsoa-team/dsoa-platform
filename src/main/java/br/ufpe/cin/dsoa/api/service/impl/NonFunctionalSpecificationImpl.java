package br.ufpe.cin.dsoa.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;


public class NonFunctionalSpecificationImpl implements NonFunctionalSpecification {
	
	private List<ConstraintImpl> attributeConstraints = new ArrayList<ConstraintImpl>();
	
	public NonFunctionalSpecificationImpl(List<ConstraintImpl> attributeConstraints) {
		super();
		this.attributeConstraints = new ArrayList<ConstraintImpl>(attributeConstraints);
	}

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.api.service.impl.NonFunctionalSpecification#getAttributeConstraints()
	 */
	@Override
	public List<ConstraintImpl> getConstraints() {
		return new ArrayList<ConstraintImpl>(attributeConstraints);
	}
}
