package br.ufpe.cin.dsoa.api.service;


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
	ServiceInstance getServiceInstance();
	
	public boolean isValid();
	
	/**
	 * Bind this Binding
	 */
	public void bind(ServiceInstance serviceInstance);
	
	/** Unbind this Binding */
	public void unbind();
	
	public boolean isBound();
	
	/**
	 * Initializes the Binding's life-cycle management
	 */
	public void start();
	
	/**
	 * Interrupts the Binding's life-cycle management
	 */
	public void stop();
}