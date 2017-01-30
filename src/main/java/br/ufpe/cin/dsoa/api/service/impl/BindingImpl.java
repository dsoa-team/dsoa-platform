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

	/**
	 * Represents the bind action (available to the executor portion of the
	 * BindingManager
	 */
	public synchronized void bind(ServiceInstanceProxy serviceInstance) {
		if (!isBound()) {
			this.serviceInstanceProxy = serviceInstance;
			this.manager.bound(serviceInstance.getName());
		}
		this.setValid(true);
	}

	/**
	 * Represents the unbind action (available to the executor portion of the
	 * BindingManager
	 */
	public synchronized void unbind() {
		if (isBound()) {
			this.setValid(false);
			this.manager.unbound(this.serviceInstanceProxy.getName());
			this.serviceInstanceProxy = null;
		}
	}

	public boolean isBound() {
		return valid && (serviceInstanceProxy != null);
	}

	public void onSet(Object pojo, String fieldName, Object value) {
		// Just do nothing...
	}

	// se chamar, a instância está válida
	public Object onGet(Object pojo, String fieldName, Object value) {
		return this.serviceInstanceProxy.getServiceObject();
	}

	public ServiceInstance getServiceInstanceProxy() {
		return this.serviceInstanceProxy;
	}

	public ComponentInstance getComponentInstance() {
		return this.componentInstance;
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
		if (!isBound()) {
			manager.selectService();
		}
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
