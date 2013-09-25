package br.ufpe.cin.dsoa.platform.handler.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.FieldInterceptor;

import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.api.service.ServiceConsumer;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.platform.handler.dependency.manager.DependencyManager;
import br.ufpe.cin.dsoa.platform.monitor.DynamicProxyFactory;

public class Dependency implements FieldInterceptor {

	private DependencyHandler handler;
	
	private ServiceConsumer  			consumer;
	private ServiceSpecification		requiredSpecification;
	private Service						service;
	private List<String>		 		blackList;

	private DependencyStatus status;
	private DependencyManager manager;

	private DynamicProxyFactory dynamicProxy;

	private EventType invocationEventType;

	public Dependency(DependencyHandler dependencyHandler, ServiceConsumer serviceConsumer,
			ServiceSpecification specification, EventType invocationEventType) {
		super();
		this.invocationEventType = invocationEventType;
		this.handler = dependencyHandler;
		this.consumer = serviceConsumer;
		this.requiredSpecification = specification;
		this.blackList = new ArrayList<String>();
		this.status = DependencyStatus.UNRESOLVED;
		this.manager = new DependencyManager(this);
		try {
			this.dynamicProxy = new DynamicProxyFactory(this, handler.getEventChannel());
		} catch (ConfigurationException e) {
			e.printStackTrace();
			dependencyHandler.setValidity(false);
		} 
	}

	public EventType getInvocationEventType() {
		return invocationEventType;
	}

	public DependencyHandler getHandler() {
		return handler;
	}

	public ServiceConsumer getConsumer() {
		return consumer;
	}

	public List<AttributeConstraint> getAttributeConstraintList() {
		return this.requiredSpecification.getNonFunctionalSpecification().getAttributeConstraints();
	}
	
	public boolean addAttributeConstraint(AttributeConstraint attributeConstraint) {
		//TODO: update query
		return this.getAttributeConstraintList().add(attributeConstraint);
	}
	
	public boolean removeAttributeConstraint(AttributeConstraint attributeConstraint) {
		//TODO: update query
		return this.getAttributeConstraintList().remove(attributeConstraint);
	}
	
	public DependencyStatus getStatus() {
		return status;
	}

	public boolean isValid() {
		return this.status == DependencyStatus.RESOLVED;
	}

	public ServiceSpecification getSpecification() {
		return this.requiredSpecification;
	}
	
	public List<String> getBlackList() {
		return blackList;
	}
	
	public Service getService() {
		return this.service;
	}
	
	public void setService(Service service) {
		if (service != this.service && this.service != null){
			this.service.ungetServiceObject();
		}
		this.service = service;
	}
	
	public void start() {
		manager.start();
	}

	public void stop() {
		manager.stop();
		this.status = DependencyStatus.UNRESOLVED;
	}

	public void computeDependencyState() {
		boolean mustCallValidate = false;
		boolean mustCallInvalidate = false;
		synchronized (this) {
			if (this.service != null) {
				if (status == DependencyStatus.UNRESOLVED) {
					status = DependencyStatus.RESOLVED;
					mustCallValidate = true;
				}
			} else {
				if (status == DependencyStatus.RESOLVED) {
					status = DependencyStatus.UNRESOLVED;
					mustCallInvalidate = true;
				}
			}
		}

		if (mustCallInvalidate) {
			invalidate();
		} else if (mustCallValidate) {
			validate();
		}

	}

	/**
	 * Calls the listener callback to notify the new state of the current
	 * dependency.
	 */
	private void invalidate() {
		handler.invalidate();
	}

	/**
	 * Calls the listener callback to notify the new state of the current
	 * dependency.
	 */
	private void validate() {
		handler.validate();
	}

	public void onSet(Object pojo, String fieldName, Object value) {
		// Just do nothing...
	}

	public Object onGet(Object pojo, String fieldName, Object value) {
		//return this.service.getServiceObject();
		return dynamicProxy.getProxy();
	}

}
