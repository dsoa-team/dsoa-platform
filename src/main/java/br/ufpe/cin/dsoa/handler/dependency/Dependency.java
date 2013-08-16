package br.ufpe.cin.dsoa.handler.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.FieldInterceptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.handler.dependency.contract.Goal;
import br.ufpe.cin.dsoa.handler.dependency.contract.ServiceConsumer;
import br.ufpe.cin.dsoa.handler.dependency.contract.ServiceProvider;
import br.ufpe.cin.dsoa.handler.dependency.manager.DependencyManager;
import br.ufpe.cin.dsoa.metric.MetricId;

public class Dependency implements FieldInterceptor {

	private DependencyHandler handler;
	
	private ServiceConsumer  		consumer;
	private String 			 		field;
	private String 					filter;
	private Class<?> 		 		specification;
	private List<Goal> 		 		goalList;
	private List<MetricId>   		metricList;
	private ServiceProvider 		serviceProvider;
	private List<ServiceReference> 	blackList;

	private ClassLoader loader;
	private DependencyStatus status;
	private DependencyManager manager;

	public Dependency(DependencyHandler dependencyHandler, ServiceConsumer serviceConsumer, String field,
			Class<?> specification, String filter, List<Goal> goalList) {
		super();
		this.handler = dependencyHandler;
		this.consumer = serviceConsumer;
		this.field = field;
		this.specification = specification;
		this.filter = filter;
		this.goalList = goalList;
		this.blackList = new ArrayList<ServiceReference>();
		this.status = DependencyStatus.UNRESOLVED;
		this.loader =  dependencyHandler.getInstanceManager().getClazz().getClassLoader();
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

	public String getFilter() {
		return filter;
	}

	public List<Goal> getGoalList() {
		return goalList;
	}
	
	public boolean addGoal(Goal goal) {
		return this.goalList.add(goal);
	}
	
	public boolean removeGoal(Goal goal) {
		return this.goalList.remove(goal);
	}
	
	public List<MetricId> getMetricList() {
		return metricList;
	}
	
	public boolean addMetric(MetricId metricId) {
		return this.metricList.add(metricId);
	}
	
	public boolean removeMetric(MetricId metricId) {
		return this.metricList.remove(metricId);
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
	
	public ClassLoader getClassloader() {
		return this.loader;
	}

	public ServiceProvider getServiceProvider() {
		return this.serviceProvider;
	}
	
	public void setServiceProvider(ServiceProvider provider) {
		if (provider != serviceProvider && serviceProvider != null){
			handler.getInstanceManager().getContext().ungetService(serviceProvider.getReference());
		}
		this.serviceProvider = provider;
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
			if (this.serviceProvider != null) {
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
		return this.serviceProvider.getServiceObject();
	}

}
