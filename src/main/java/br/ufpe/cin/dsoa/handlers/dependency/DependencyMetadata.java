package br.ufpe.cin.dsoa.handlers.dependency;

import java.util.List;

import br.ufpe.cin.dsoa.contract.Slo;

public class DependencyMetadata {

	private Class<?> specification;
	private List<Slo> slos;

	public DependencyMetadata(Class<?> specification, List<Slo> slos) {
		this.specification = specification;
		this.slos = slos;
	}

	public List<Slo> getSlos() {
		return slos;
	}

	public Class<?> getSpecification() {
		return specification;
	}

}
