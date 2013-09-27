package br.ufpe.cin.dsoa.platform.handler.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.FieldInterceptor;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.platform.handler.dependency.manager.DependencyManager;
import br.ufpe.cin.dsoa.platform.monitor.DynamicProxyFactory;

public class Dependency implements FieldInterceptor {

	private DependencyHandler 			handler;
	
	private ServiceSpecification		requiredSpecification;
	
	private Service						service;
	
	private List<String>		 		blackList;
	
	private String componentId;
	
	private DependencyStatus status;
	
	private DependencyManager manager;

	private DynamicProxyFactory dynamicProxy;

	public Dependency(DependencyHandler dependencyHandler, String componentId, ServiceSpecification specification) {
		super();
		this.handler = dependencyHandler;
		this.componentId = componentId;
		this.requiredSpecification = specification;
		this.blackList = new ArrayList<String>();
		this.status = DependencyStatus.UNRESOLVED;
		this.manager = new DependencyManager(this);
	}
	
	public void setDynamicProxy(DynamicProxyFactory dynamicProxyFactory){
		this.dynamicProxy = dynamicProxyFactory;
	}

	public void start() {
		manager.resolve();
	}

	public void stop() {
		manager.release();
		this.status = DependencyStatus.UNRESOLVED;
	}
	
	public String getComponentId() {
		return componentId;
	}
	
	public DependencyHandler getHandler() {
		return handler;
	}

	public List<AttributeConstraint> getAttributeConstraintList() {
		return this.requiredSpecification.getNonFunctionalSpecification().getAttributeConstraints();
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
