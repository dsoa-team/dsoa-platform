package br.ufpe.cin.dsoa.platform.handler.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.FieldInterceptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.platform.handler.dependency.manager.DependencyManager;
import br.ufpe.cin.dsoa.service.AttributeConstraint;
import br.ufpe.cin.dsoa.service.Service;
import br.ufpe.cin.dsoa.service.ServiceConsumer;

public class Dependency implements FieldInterceptor {

	private DependencyHandler handler;
	
	private ServiceConsumer  			consumer;
	private String 			 			field;
	private Class<?> 		 			specification;
	private List<AttributeConstraint> 	attConstraintList;
	private Service						service;
	private List<ServiceReference> 		blackList;

	private DependencyStatus status;
	private DependencyManager manager;

	public Dependency(DependencyHandler dependencyHandler, ServiceConsumer serviceConsumer, String field,
			Class<?> specification, List<AttributeConstraint> attConstraintList) {
		super();
		this.handler = dependencyHandler;
		this.consumer = serviceConsumer;
		this.field = field;
		this.specification = specification;
		this.attConstraintList = attConstraintList;
		this.blackList = new ArrayList<ServiceReference>();
		this.status = DependencyStatus.UNRESOLVED;
		this.manager = new DependencyManager(this);
	}

	public BundleContext getContext() {
		return handler.getInstanceManager().getContext();
	}
	
	public DependencyHandler getHandler() {
		return handler;
	}

	public ServiceConsumer getConsumer() {
		return consumer;
	}

	public String getField() {
		return field;
	}

	public List<AttributeConstraint> getAttributeConstraintList() {
		return attConstraintList;
	}
	
	public boolean addAttributeConstraint(AttributeConstraint attributeConstraint) {
		return this.attConstraintList.add(attributeConstraint);
	}
	
	public boolean removeAttributeConstraint(AttributeConstraint attributeConstraint) {
		return this.attConstraintList.remove(attributeConstraint);
	}
	
	public DependencyStatus getStatus() {
		return status;
	}

	public boolean isValid() {
		return this.status == DependencyStatus.RESOLVED;
	}

	public Class<?> getSpecification() {
		return this.specification;
	}
	
	public String getSpecificationName() {
		return this.specification.getName();
	}
	
	public List<ServiceReference> getBlackList() {
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
		return this.service.getServiceObject();
	}

}
