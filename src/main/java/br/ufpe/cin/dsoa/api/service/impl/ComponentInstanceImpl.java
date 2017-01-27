package br.ufpe.cin.dsoa.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.core.impl.NamedElementImpl;
import br.ufpe.cin.dsoa.api.qos.QoSLibrary;
import br.ufpe.cin.dsoa.api.service.Binding;
import br.ufpe.cin.dsoa.api.service.ComponentInstance;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentInstanceManager;
import br.ufpe.cin.dsoa.platform.component.autonomic.DsoaComponentInstanceAutonomicManager;
import br.ufpe.cin.dsoa.platform.handler.requires.DsoaBindingManager;

/**
 * This is a Meta-Object that intends to provide a management interface to an Autonomic Manager in
 * a platform independent way. It implements an Executor interface (vide MAPE-K model) by delegating
 * management actions to the iPojo framework. Observe that the autonomic manager interacts with this
 * component through an iPojo independent interface, that is carefully designed in order to just
 * represent actions that make sense on any Component Instance.  
 * 
 * @author fabions
 * 
 */
public class ComponentInstanceImpl extends NamedElementImpl implements ComponentInstance {

	/*
	 * Management related objects. 
	 */
	
	/**
	 * The autonomic manager responsible for managing corresponding component instance.
	 * It uses this ComponentInstance Meta-Object in order to perform management actions in an iPojo independent way. 
	 */
	private DsoaComponentInstanceAutonomicManager dsoaManager;
	
	/**
	 * The iPojo instance manager. It is used in order to interact with the iPojo platform in order to
	 * realize the autonomicManager decisions 
	 */
	private DsoaComponentInstanceManager ipojoManager;
	
	/*
	 * Part of this meta-object interface. These are meta-data that are operated upon in order to
	 * produce effects on the Base plan (Vide Reflection model)
	 */
	
	/**
	 * This ComponentInstance's type (ComponentType). See Dsoa-Metamodel 
	 */
	private ComponentTypeImpl componentType;
	
	/**
	 * This ComponentInstance's bindings
	 */
	private Map<String, Binding> bindingMap;
	
	/**
	 * This ComponentInstance's serviceInstances
	 */
	private Map<String, ServiceInstance> serviceInstanceMap;

	/**
	 * QoS Library
	 */
	private QoSLibrary qosLib;
	
	/**
	 * 
	 * @param instanceManager: The autonomic manager responsible for managing this instance 
	 * @param name
	 * @param componentType
	 */
	public ComponentInstanceImpl(String name, ComponentTypeImpl componentType, DsoaComponentInstanceManager ipojoManager, DsoaComponentInstanceAutonomicManager dsoaManager) {
		super(name);
		this.componentType = componentType;
		this.bindingMap = new HashMap<String, Binding>();
		this.serviceInstanceMap = new HashMap<String, ServiceInstance>();
		this.ipojoManager = ipojoManager;
		this.dsoaManager = dsoaManager;
	}
	
	public void setQosLib(QoSLibrary lib) {
		this.qosLib = lib;
	}
	
	public QoSLibrary getQosLib() {
		return this.qosLib;
	}
	

	public ComponentTypeImpl getComponentType() {
		return componentType;
	}

	public DsoaBindingManager addBinding(BindingImpl bindingInstance) {
		this.bindingMap.put(bindingInstance.getPort().getName(), bindingInstance);
		return this.dsoaManager.getBindingManager(bindingInstance);
	}

	public void addServiceInstance(ServiceInstanceImpl serviceInstance) {
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
		return this.ipojoManager;
	}
}
