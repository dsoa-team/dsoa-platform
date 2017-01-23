package br.ufpe.cin.dsoa.api.service;

import java.util.List;

public interface DsoaComponentInstance extends NamedElement {
	public DsoaComponentType getComponentType();
	
	public Binding getBinding(String portName);
	public ServiceInstance getServiceInstance(String portName);
	
	public List<Binding> getBindingList();
	public List<ServiceInstance> getServiceInstanceList();

	public void addBinding(Binding bindingInstance);
	public void addServiceInstance(ServiceInstance serviceInstance);
}
