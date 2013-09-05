package br.ufpe.cin.dsoa.platform.registry;

import java.util.List;

import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;

public interface ServiceRegistry {
	

	public void getBestService(ServiceSpecification specification,
			List<String> blackList, ServiceListener listener);
	
	public boolean addService(Service service);

	public Service getService(String id);
	
	public List<Service> getServices();
	
	public List<Service> getServices(String[] classNames);

	/*
	private Map<String,Service> serviceMap = new HashMap<String, Service>();
	
	public synchronized boolean addService(Service service) {
		boolean result = false;
		String id = service.getServiceId();
		if (!serviceMap.containsKey(id)) {
			serviceMap.put(id, service);
			result = true;
		}
		return result;
	}
	
	public Service getService(String id) {
		return serviceMap.get(id);
	}
	*/
}
