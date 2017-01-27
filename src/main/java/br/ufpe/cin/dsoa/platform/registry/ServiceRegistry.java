package br.ufpe.cin.dsoa.platform.registry;

import java.util.List;

import br.ufpe.cin.dsoa.api.service.impl.ServiceSpecification;
import br.ufpe.cin.dsoa.platform.component.autonomic.DsoaServiceListener;

public interface ServiceRegistry {
	

	public void getBestService(ServiceSpecification specification,
			List<String> blackList, DsoaServiceListener listener);
	
	public void waitForService(ServiceSpecification specification,
			DsoaServiceListener listener, List<String> blackList) throws InvalidServiceSpecificationException;

}
