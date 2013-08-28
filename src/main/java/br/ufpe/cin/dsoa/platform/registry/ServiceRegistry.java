package br.ufpe.cin.dsoa.platform.registry;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.service.Service;

public class ServiceRegistry {
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
}
