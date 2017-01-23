package br.ufpe.cin.dsoa.api.service;

public interface ServiceInstance extends PortInstance {
	
	/** Corresponding ProvidedPort */
	Port getPort();
	
	public Object getServiceObject();

	public void ungetServiceObject();
}
