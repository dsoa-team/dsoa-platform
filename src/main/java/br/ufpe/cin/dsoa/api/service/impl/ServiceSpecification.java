package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.service.FunctionalInterface;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;

public interface ServiceSpecification {

	public NonFunctionalSpecification getNonFunctionalSpecification();

	public FunctionalInterface getFunctionalInterface();

}