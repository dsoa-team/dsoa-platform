package br.ufpe.cin.dsoa.management.service;

import java.util.Collection;

public interface ServiceCatalog {
	
	public void addService(ManagedService service);
	
	public abstract Collection<ManagedService> getServices();	
}
