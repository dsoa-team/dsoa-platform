package br.ufpe.cin.dsoa.platform.service.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.selector.RankStrategy;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public class SimpleAdditiveWeighting implements RankStrategy {

	@Override
	public ServiceInstance ranking(List<Constraint> constraints,
			List<ServiceInstance> candidates) {
		// Filter undesired services
		List<ServiceInstance> filteredCandidates =  filterCandidates(constraints, candidates);
		
		// Normalize attribute values
		double[][] normalized = Normalizer.normalize(constraints, filteredCandidates);
		
		// Totalize the weight
		double sumWeight = 0; 
		for (Constraint c : constraints) {
			sumWeight += c.getWeight();
		}
		
		Map<Double, ServiceInstance> weighted = new HashMap<Double, ServiceInstance>();
		
		//x indice dos candidates
		for(int x = 0; x < normalized.length; x++) {
			double sum = 0;
			for(int y = 0; y < normalized[0].length; y++) {
				Constraint requiredConstraint = constraints.get(y);
				double normWeight = requiredConstraint.getWeight()/sumWeight;
				sum += normWeight * normalized[x][y];
			}
			
			weighted.put(sum, filteredCandidates.get(x));
		}
		
		double max = Collections.max(weighted.keySet());
		
		return weighted.get(max);
	}

	/**
	 * Filtra candidatos que não dispoem de informacoes a respeito do atritudo
	 * requerido
	 * 
	 * @param constraints
	 * @param candidates
	 * @return
	 */
	private List<ServiceInstance> filterCandidates(
			List<Constraint> constraints, List<ServiceInstance> candidates) {

		List<ServiceInstance> filtered = new ArrayList<ServiceInstance>();

		for (ServiceInstance candidate : candidates) {
			List<Constraint> candidateConstraints = candidate.getPort()
					.getServiceSpecification().getNonFunctionalSpecification()
					.getConstraints();
			Map<String, Constraint> mapConstraint = Normalizer
					.toMap(candidateConstraints);

			boolean valid = true;
			for (Constraint required : constraints) {
				if (!mapConstraint.containsKey(Normalizer
						.constraintKey(required))) {
					valid = false;
				}
			}

			if (valid) {
				filtered.add(candidate);
			} else {
				valid = true;
			}

		}

		return filtered;
	}

}