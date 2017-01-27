package br.ufpe.cin.dsoa.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.core.impl.NamedElementImpl;
import br.ufpe.cin.dsoa.api.qos.QoSLibrary;
import br.ufpe.cin.dsoa.api.service.Binding;
import br.ufpe.cin.dsoa.api.service.DsoaComponentInstance;
import br.ufpe.cin.dsoa.api.service.DsoaComponentType;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentInstanceManager;

public class DsoaComponentInstanceImpl extends NamedElementImpl implements DsoaComponentInstance {

	private DsoaComponentType componentType;
	private Map<String, Binding> bindingMap;
	private Map<String, ServiceInstance> serviceInstanceMap;

	private DsoaComponentInstanceManager instanceManager;
	
	private QoSLibrary qosLib;
	
	public void setQosLib(QoSLibrary lib) {
		this.qosLib = lib;
	}
	
	public QoSLibrary getQosLib() {
		return this.qosLib;
	}
	
	/**
	 * 
	 * @param instanceManager: The autonomic manager responsible for managing this instance 
	 * @param name
	 * @param componentType
	 */
	public DsoaComponentInstanceImpl(DsoaComponentInstanceManager instanceManager, String name, DsoaComponentType componentType) {
		super(name);
		this.componentType = componentType;
		this.bindingMap = new HashMap<String, Binding>();
		this.serviceInstanceMap = new HashMap<String, ServiceInstance>();
		this.instanceManager = instanceManager;
	}

	public DsoaComponentType getComponentType() {
		return componentType;
	}

	@Override
	public void addBinding(Binding bindingInstance) {
		this.bindingMap.put(bindingInstance.getPort().getName(), bindingInstance);
	}

	@Override
	public void addServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstanceMap.put(serviceInstance.getPort().getName(), serviceInstance);
	}

	@Override
	public Binding getBinding(String portName) {
		return this.bindingMap.get(portName);
	}

	@Override
	public ServiceInstance getServiceInstance(String portName) {
		return this.serviceInstanceMap.get(portName);
	}

	@Override
	public List<Binding> getBindingList() {
		return new ArrayList<Binding>(this.bindingMap.values());
	}

	@Override
	public List<ServiceInstance> getServiceInstanceList() {
		return new ArrayList<ServiceInstance>(this.serviceInstanceMap.values());
	}

	public DsoaComponentInstanceManager getInstanceManager() {
		return this.instanceManager;
	}
}
