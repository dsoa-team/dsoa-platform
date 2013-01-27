package br.ufpe.cin.dsoa.handlers.dependency;

import br.ufpe.cin.dsoa.contract.Service;
import br.ufpe.cin.dsoa.contract.ServiceMetadata;

public class ServiceModel {
	private Service service;
	private ServiceMetadata metadata;
	
	ServiceModel() {}
	
	public ServiceModel(Service service, ServiceMetadata metadata) {
		super();
		this.service = service;
		this.metadata = metadata;
	}

	public Service getService() {
		return service;
	}

	public ServiceMetadata getMetadata() {
		return metadata;
	}
	
	
}
