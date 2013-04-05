package br.ufpe.cin.dsoa.management;

import java.util.Collection;

public interface ManagedServiceRegistry {
	
	public void addService(ManagedService service);
	
	public ManagedService getService(String id);
	
	public abstract Collection<ManagedService> getServices();	
}
