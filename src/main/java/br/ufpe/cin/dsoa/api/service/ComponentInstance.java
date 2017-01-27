package br.ufpe.cin.dsoa.api.service;

import java.util.List;

import br.ufpe.cin.dsoa.api.core.NamedElement;
import br.ufpe.cin.dsoa.api.qos.QoSLibrary;

public interface ComponentInstance extends NamedElement {
	public ComponentType getComponentType();
	
	public Binding getBinding(String portName);
	public ServiceInstance getServiceInstance(String portName);
	
	public List<Binding> getBindingList();
	public List<ServiceInstance> getServiceInstanceList();

	/*
	 * JUST ON THE IMPLEMENTATION! THIS INTERFACE SHOULD CONTAIN ONLY GETTERS
	public void addBinding(Binding bindingInstance);
	public void addServiceInstance(ServiceInstance serviceInstance);
	*/
	
	public QoSLibrary getQosLib();
	
}
