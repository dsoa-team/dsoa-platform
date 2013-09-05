package br.ufpe.cin.dsoa.api.service;

import java.util.ArrayList;
import java.util.List;

public class NonFunctionalSpecification {
	
	private List<AttributeConstraint> attributeConstraints = new ArrayList<AttributeConstraint>();
	
	public NonFunctionalSpecification(List<AttributeConstraint> attributeConstraints) {
		super();
		this.attributeConstraints = new ArrayList<AttributeConstraint>(attributeConstraints);
	}

	public List<AttributeConstraint> getAttributeConstraints() {
		return new ArrayList<AttributeConstraint>(attributeConstraints);
	}
}
