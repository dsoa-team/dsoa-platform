package br.ufpe.cin.dsoa.platform.registry.rank;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.platform.registry.normalizer.Normalizer;



public interface Rank {
	
	//public ServiceReference ranking(List<Slo> slos, double[][] matrix, ServiceReference... references);
	public ServiceReference ranking(List<AttributeConstraint> slos, Normalizer normalizer, ServiceReference... references);

}
