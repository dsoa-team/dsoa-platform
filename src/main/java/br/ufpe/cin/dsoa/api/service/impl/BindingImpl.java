package br.ufpe.cin.dsoa.api.service.impl;

import java.util.List;

import org.apache.felix.ipojo.FieldInterceptor;

import br.ufpe.cin.dsoa.api.service.Binding;
import br.ufpe.cin.dsoa.api.service.ComponentInstance;
import br.ufpe.cin.dsoa.api.service.Port;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.platform.component.autonomic.DsoaBindingManager;
import br.ufpe.cin.dsoa.platform.handler.requires.DsoaRequiresHandler;
import br.ufpe.cin.dsoa.util.Constants;

/**
 * Represents an Binding implementation
 * 
 * @author fabions
 * 
 */
public class BindingImpl extends PortInstanceImpl implements Binding,
		FieldInterceptor {
	
	/**
	 * A meta-object that represents the component instance which owns this
	 * binding
	 */
	private ComponentInstanceImpl componentInstance;

	/**
	 * A meta-model object that represents the bound service instance
	 */
	private ServiceInstanceProxy serviceInstanceProxy;

	/**
	 * A reference to the autonomic manager, which is responsible for managing
	 * this binding
	 */
	private DsoaBindingManager manager;

	/**
	 * Points to the handler responsible for managing this dependency
	 */
	private DsoaRequiresHandler handler;

	/**
	 * The binding's status
	 */
	private boolean valid;
	
	private Object bindingLock = new Object();
	
	public BindingImpl(DsoaRequiresHandler handler,
			ComponentInstanceImpl componentInstance, Port port,
			List<br.ufpe.cin.dsoa.api.service.Property> props) {
		// Sets the name of this binding to the following format:
		// <component-instance-name>.<required-port-name>
		super(componentInstance.getName() + Constants.TOKEN + port.getName(), port, props);

		this.handler = handler;

		this.componentInstance = componentInstance;

		// Add this Binding to the ComponentInstance to keep consistency
		this.manager = this.componentInstance.addBinding(this);
		
	}

	public ServiceInstance getServiceInstanceProxy() {
		return this.serviceInstanceProxy;
	}

	public ComponentInstance getComponentInstance() {
		return this.componentInstance;
	}

	
	/**
	 * This method is inherited from FieldInterceptor interface.
	 * It is called by the instance manager while a field is accessed
	 * in order to allow an Interceptor to return the object to which
	 * a message should be forwarded. Here, the Binding meta-object returns
	 * the Service Instance to which it is currently bound to. More precisely,
	 * it returns a proxy that refers to the real service pointed by that instance.
	 * This proxy is instrumented in order to generate InvocationEvents that can be
	 * processed to get quality-related information. This instance is only changed
	 * when that service is removed from the registry or when it is not able to 
	 * satisfy stated constraints anymore.
	 * 
	 * @see org.apache.felix.ipojo.FieldInterceptor#onGet(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	// se chamar, a instância está válida
	public Object onGet(Object pojo, String fieldName, Object value) {
		synchronized (bindingLock) {
			while (this.serviceInstanceProxy == null) {
				try {
					bindingLock.wait();
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
			return this.serviceInstanceProxy.getServiceObject();
		}
	}
	
	public void onSet(Object pojo, String fieldName, Object value) {
		// Just do nothing...
	}

	/*
	 * ===============================================================================
	 * The following methods are responsible for managing Binding status.
	 * They have being made synchronized in order to guarantee consistence.
	 * ===============================================================================
	 */
	/**
	 * Binds this Binding meta-object to the referred serviceInstance, which is, in fact,
	 * a proxy to a registered service. This proxy is instrumented to generate
	 * InvocationEvents. 
	 */
	public void bind(ServiceInstanceProxy serviceInstance) {
		synchronized (bindingLock) {
			if (this.serviceInstanceProxy == null) {
				this.serviceInstanceProxy = serviceInstance;
				this.manager.bound(serviceInstance.getName());
				this.setValid(true);
				bindingLock.notifyAll();
			}
		}
	}

	/**
	 * Represents the unbind action (available to the executor portion of the
	 * BindingManager
	 */
	public void unbind() {
		synchronized (bindingLock) {
			this.setValid(false);
			if (this.serviceInstanceProxy != null) {
				this.manager.unbound(this.serviceInstanceProxy.getName());
				this.serviceInstanceProxy = null;
			}
		}
	}

	@Override
	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
		if (valid && !handler.isValid()) {
			handler.computeState();
		} else {
			if(!valid && handler.isValid())
				handler.setValidity(false);
		}
	}
	

	/**
	 * 
	 * This method is called by the DsoaRequiresHandler in order
	 * to tell the Binding Meta-Object to try to bind it self.
	 * At that time, this meta-object calls its autonomic manager
	 * in order to select an adequate ServiceInstanceProxy, which
	 * this Binding can be connected to considering its requirements, as 
	 * stated through corresponding Port specifications.
	 *  
	 */
	public void start() {
		if (this.serviceInstanceProxy == null) {
			manager.selectService();
		}
		this.setValid(true);
	}

	/**
	 *  This method is called by the DsoaRequiresHandler in order
	 * to tell the Binding Meta-Object to try to unbind it self releasing
	 * the ServiceInstanceProxy that it maintains. When this is done, the Binding
	 * shall call its manager in order to search for another ServiceInstance.
	 * 
	 */
	public void stop() {
		this.unbind();
		//manager.selectService();
	}

}
