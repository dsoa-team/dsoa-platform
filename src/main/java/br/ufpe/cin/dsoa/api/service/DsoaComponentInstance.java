package br.ufpe.cin.dsoa.api.service;

import java.util.List;

import br.ufpe.cin.dsoa.api.core.NamedElement;
import br.ufpe.cin.dsoa.api.qos.QoSLibrary;

public interface DsoaComponentInstance extends NamedElement {
	public DsoaComponentType getComponentType();
	
	public Binding getBinding(String portName);
	public ServiceInstance getServiceInstance(String portName);
	
	public List<Binding> getBindingList();
	public List<ServiceInstance> getServiceInstanceList();

	public void addBinding(Binding bindingInstance);
	public void addServiceInstance(ServiceInstance serviceInstance);
	
	
	public QoSLibrary getQosLib();
	public void setQosLib(QoSLibrary lib);

}
