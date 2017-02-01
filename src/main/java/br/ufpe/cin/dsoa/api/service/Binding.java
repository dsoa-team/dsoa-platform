package br.ufpe.cin.dsoa.api.service;

import br.ufpe.cin.dsoa.api.service.impl.ServiceInstanceProxy;



/**
 * Represents a Binding and has a reference to a provided service (Endpoint)
 * instance
 * 
 * @author fabions
 */
public interface Binding extends PortInstance {

	/** Corresponding RequiredPort */
	Port getPort();
	
	/** Represents the bounded service */
	ServiceInstance getServiceInstanceProxy();
	
	public boolean isValid();
	
	/**
	 * Bind this Binding
	 */
	public void bind(ServiceInstanceProxy serviceInstance);
	
	/** Unbind this Binding */
	public void unbind();
	
	/**
	 * Initializes the Binding's life-cycle management
	 */
	public void start();
	
	/**
	 * Interrupts the Binding's life-cycle management
	 */
	public void stop();

	ComponentInstance getComponentInstance();
}