package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeNotificationListener;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.handler.dependency.Dependency;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.platform.registry.ServiceRegistry;


public class DependencyManager implements ServiceListener, AttributeNotificationListener {

	/**
	 * The component responsible for service selection.
	 */
	private BundleContext context;

	/**
	 * The managed dependency
	 */
	private Dependency dependency;
	//private DependencyManagerMBean dependencys
	
	private ServiceRegistry serviceRegistry;
	private ServiceTracker brokerTracker;
	private ServiceTracker attributeEventMapperCatalogTacker;
	
	private Analyzer analyzer;

	private Service service;
	//private Planner planner;

	public AttributeEventMapperCatalog attributeEventMapperCatalog;
	
	public DependencyManager(Dependency dependency) {
		this.context = dependency.getHandler().getInstanceManager().getContext();
		this.dependency = dependency;
		this.analyzer = new EsperAnalyzer();
		this.brokerTracker = new ServiceTracker(this.context, ServiceRegistry.class.getName(), new BrokerTrackerCustomizer());
		this.attributeEventMapperCatalogTacker = new ServiceTracker(this.context, AttributeEventMapperCatalog.class.getName(), new AttributeEventMapperCatalogCustomizer());
	}

	public void start() {
		this.brokerTracker.open();
		this.attributeEventMapperCatalogTacker.open();
	}

	public void stop() {
		brokerTracker.close();
		this.attributeEventMapperCatalogTacker.close();
		release();
	}
	
	public void resolve() {
		if (serviceRegistry != null) {
			serviceRegistry.getBestService(dependency.getSpecification(), dependency.getBlackList(), this);
		} 
	}

	public void release() {
		dependency.setService(null);
		dependency.computeDependencyState();
	}
	
	public String getServiceInterface() {
		return dependency.getSpecification().getServiceInterface();
	}
	
	public void onArrival(Service service) {
		this.service = service;
		if (this.attributeEventMapperCatalog != null) {
			initializeManagement();
		}
	}

	public void onDeparture(Service service) {
		this.analyzer.stop();
		release();
		resolve();
	}
	

	public void onError(Exception e) {
		// TODO Auto-generated method stub
		
	}
	
	class BrokerTrackerCustomizer implements ServiceTrackerCustomizer {
		public Object addingService(ServiceReference reference) {
			synchronized(DependencyManager.this) {
				if (serviceRegistry == null) {
					serviceRegistry = (ServiceRegistry) DependencyManager.this.context.getService(reference);
				}
				serviceRegistry.getBestService(dependency.getSpecification(), dependency.getBlackList(), DependencyManager.this);
				return serviceRegistry;
			}
		}

		public void modifiedService(ServiceReference reference, Object service) {
			// Just do nothing!
		}

		public void removedService(ServiceReference reference, Object service) {
			synchronized(DependencyManager.this) {
				DependencyManager.this.context.ungetService(reference);
				serviceRegistry = null;
			}
		}	
	}
	
	public void initializeManagement() {
		EventProcessingService epService = this.getEventProcessingService();
		analyzer.start(service.getCompomentId(), dependency.getAttributeConstraintList(), attributeEventMapperCatalog,epService, this );
		dependency.setService(service);
		dependency.computeDependencyState();
	}
	
	private EventProcessingService getEventProcessingService() {
		ServiceReference reference = context.getServiceReference(EventProcessingService.class.getName());
		EventProcessingService epService = (EventProcessingService) context.getService(reference);
		
		return epService;
	}

	class AttributeEventMapperCatalogCustomizer implements ServiceTrackerCustomizer {

		@Override
		public Object addingService(ServiceReference reference) {
			attributeEventMapperCatalog = (AttributeEventMapperCatalog) context.getService(reference);
			if (service != null) {
				initializeManagement();
			}
			return reference;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {
			// TODO Auto-generated method stub
		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			// TODO Auto-generated method stub
		}
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
	
	/*public ServiceConsumer getConsumer() {
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
