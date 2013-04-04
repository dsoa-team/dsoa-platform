package br.ufpe.cin.dsoa.management.service;

import java.util.Collection;

public interface ManagedServiceCatalog {
	
	public void addService(ManagedService service);
	
	public Collection<ManagedService> getServices();
	
	public ManagedService getService(String serviceId);
	
}
