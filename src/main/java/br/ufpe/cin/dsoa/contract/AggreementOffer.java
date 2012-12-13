package br.ufpe.cin.dsoa.contract;

import java.util.List;

public class AggreementOffer {
	
	/* Interface do serviço */
	private Class<?> specification;

	/* Lista de requisitos não funcionais */
	private List<Slo> slos;
	
	public AggreementOffer(String qosMode,
			Class<?> specification, List<Slo> slos) {
		this.specification = specification;
		this.slos = slos;
	}

	public Class<?> getSpecification() {
		return specification;
	}

	public List<Slo> getSlos() {
		return slos;
	}

}
