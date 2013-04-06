package br.ufpe.cin.dsoa.management;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ManagedServiceCatalogImpl implements ManagedServiceRegistry {

	private Map<String, ManagedService> registry;

	public ManagedServiceCatalogImpl() {
		this.registry = new HashMap<String, ManagedService>();
	}

	public Collection<ManagedService> getServices() {
		return this.registry.values();

	}
	
	public void start() {
		
	}
	
	public void stop() {
		
	}

	public void addService(ManagedService service) {
		this.registry.put(service.getId(), service);
	}

	public ManagedService getService(String id) {
		return this.registry.get(id);
	}
	
}
