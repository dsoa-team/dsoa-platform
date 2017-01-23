package br.ufpe.cin.dsoa.api.service;

import java.util.List;

import br.ufpe.cin.dsoa.api.service.impl.ConstraintImpl;

public interface NonFunctionalSpecification {

	public List<ConstraintImpl> getConstraints();

}