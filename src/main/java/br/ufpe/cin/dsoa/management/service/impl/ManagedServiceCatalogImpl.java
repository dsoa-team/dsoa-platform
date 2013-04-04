package br.ufpe.cin.dsoa.management.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.configurator.hook.DsoaServiceListener;
import br.ufpe.cin.dsoa.management.service.ManagedService;
import br.ufpe.cin.dsoa.management.service.ManagedServiceCatalog;

public class ManagedServiceCatalogImpl implements ManagedServiceCatalog {

	private DsoaServiceListener listener;
	private Map<String, ManagedService> managedServiceMap;
	
	public ManagedServiceCatalogImpl(BundleContext ctx) {
		this.managedServiceMap = new ConcurrentHashMap<String, ManagedService>();
		this.listener = new DsoaServiceListener(ctx);
		this.listener.start();
	}
	
	public void addService(ManagedService service) {
		this.managedServiceMap.put(service.getId(), service);
	}

	public Collection<ManagedService> getServices() {
		return this.managedServiceMap.values();
		
	}

	public ManagedService getService(String serviceId) {
		return this.managedServiceMap.get(serviceId);
	}
	
}
