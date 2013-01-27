package br.ufpe.cin.dsoa.broker.rank;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.broker.normalizer.Normalizer;
import br.ufpe.cin.dsoa.contract.Slo;



public interface Rank {
	
	//public ServiceReference ranking(List<Slo> slos, double[][] matrix, ServiceReference... references);
	public ServiceReference ranking(List<Slo> slos, Normalizer normalizer, ServiceReference... references);

}
