package br.ufpe.cin.dsoa.platform.resource;


import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public interface ResourceManager {

	ManagedService manage(ServiceInstance service);

	void release(String serviceId);
	
	void manage(EventProcessingAgent agent);

	void release(EventProcessingAgent agent);

}