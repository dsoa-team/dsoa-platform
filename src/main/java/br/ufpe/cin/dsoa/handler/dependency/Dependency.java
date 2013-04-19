package br.ufpe.cin.dsoa.handler.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.FieldInterceptor;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.Constraint;
import br.ufpe.cin.dsoa.contract.ServiceConsumer;

public class Dependency implements FieldInterceptor {

	private DependencyHandler handler;
	private ServiceConsumer consumer;
	private String field;
	private Class<?> specification;
	private String filter;
	private List<Constraint> constraintList;
	private List<ServiceReference> blackList;
	private DependencyStatus status;
	private Object serviceObject;
	private DependencyManager manager;

	public Dependency(DependencyHandler dependencyHandler, ServiceConsumer serviceConsumer, String field,
			Class<?> specification, String filter, List<Constraint> constraintList) {
		super();
		this.handler = dependencyHandler;
		this.consumer = serviceConsumer;
		this.field = field;
		this.specification = specification;
		this.filter = filter;
		this.constraintList = constraintList;
		this.blackList = new ArrayList<ServiceReference>();
		this.status = DependencyStatus.UNRESOLVED;
		this.manager = new DependencyManager(this, dependencyHandler.getInstanceManager());
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

	public String getFilter() {
		return filter;
	}

	public List<Constraint> getConstraintList() {
		return constraintList;
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

	public Object getServiceObject() {
		return serviceObject;
	}

	void setServiceObject(Object serviceObject) {
		this.serviceObject = serviceObject;
	}
	
	public void start() {
		manager.resolve();
	}

	public void stop() {
		manager.release();
		this.status = DependencyStatus.UNRESOLVED;
	}

	void computeDependencyState() {
		boolean mustCallValidate = false;
		boolean mustCallInvalidate = false;
		synchronized (this) {
			if (this.serviceObject != null) {
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

	@Override
	public void onSet(Object pojo, String fieldName, Object value) {
		// Just do nothing...
	}

	@Override
	public Object onGet(Object pojo, String fieldName, Object value) {
		return this.serviceObject;
	}

}
