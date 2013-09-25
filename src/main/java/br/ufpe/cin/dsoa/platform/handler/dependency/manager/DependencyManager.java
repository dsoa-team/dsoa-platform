package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeNotificationListener;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.handler.dependency.Dependency;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.platform.registry.ServiceRegistry;


public class DependencyManager implements ServiceListener, AttributeNotificationListener {

	/**
	 * The managed dependency
	 */
	private Dependency dependency;
	
	private DsoaPlatform dsoa;

	public DependencyManager(Dependency dependency) {
		this.dependency = dependency;
		this.dsoa = dependency.getHandler().getDsoaPlatform();
	}

	public void resolve() {
		dsoa.getServiceRegistry().getBestService(dependency.getSpecification(), dependency.getBlackList(), this);
	}

	public void release() {
		dependency.setService(null);
		dependency.computeDependencyState();
	}
	
	public String getServiceInterface() {
		return dependency.getSpecification().getServiceInterface();
	}
	
	public void onArrival(Service service) {
		dsoa.getAnalyzer().start(service.getCompomentId(), dependency.getAttributeConstraintList(), this );
		dependency.setService(service);
		dependency.computeDependencyState();
	}

	public void onDeparture(Service service) {
		release();
		resolve();
	}

	public void onError(Exception e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void handleNotification(AttributeConstraint constraint, AttributeValue value) {

			System.out.println("Constraint: " + constraint);
			System.out.println("Value: "+ value);
	}

/*	class BrokerTrackerCustomizer implements ServiceTrackerCustomizer {
		@Override
		public Object addingService(ServiceReference reference) {
			if (broker == null) {
				broker = (Broker) dependency.getContext().getService(reference);
			}
			
			synchronized (waiting) {
				if (waiting) {
					broker.getBestService(dependency.getContext(), dependency.getSpecificationName(), dependency.getConstraintList(),
						dependency.getBlackList(), DependencyManager.this);
					waiting = Boolean.FALSE;
				}
			}
			return broker;
		}*/
	
	/*public ServiceComposition getConsumer() {
		return consumer;
	}

	public String getField() {
		return field;
	}

	public String getFilter() {
		return filter;
	}

	public List<AttributeConstraint> getGoalList() {
		return goalList;
	}
	
	public boolean addGoal(AttributeConstraint goal) {
		return this.goalList.add(goal);
	}
	
	public boolean removeGoal(AttributeConstraint goal) {
		return this.goalList.remove(goal);
	}
	
	public List<AttributeId> getMetricList() {
		return metricList;
	}
	
	public boolean addMetric(AttributeId metricId) {
		return this.metricList.add(metricId);
	}
	
	public boolean removeMetric(AttributeId metricId) {
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
	}*/
	
	/*	
		public Object getService() {
			if (this.realService == null) {
				this.realService = context.getService(serviceProvider);
				if (this.realService instanceof IPOJOServiceFactory) {
					this.realService = ((IPOJOServiceFactory) this.realService).getService(instanceManager);
				}
			}
			return this.realService;
		}
	*/
}
