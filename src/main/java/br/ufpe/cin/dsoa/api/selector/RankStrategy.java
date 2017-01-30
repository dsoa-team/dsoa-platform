package br.ufpe.cin.dsoa.api.selector;

import java.util.List;

import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public interface RankStrategy {

	public ServiceInstance ranking(List<Constraint> requiredConstraints,
			List<ServiceInstance> candidates);

}