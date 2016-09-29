package br.ufpe.cin.dsoa.platform.registry;

import java.util.List;

import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;

public interface ServiceRegistry {
	

	public void getBestService(ServiceSpecification specification,
			List<String> blackList, ServiceListener listener);
	
	public void waitForService(ServiceSpecification specification,
			ServiceListener listener, List<String> blackList) throws InvalidServiceSpecificationException;

}
