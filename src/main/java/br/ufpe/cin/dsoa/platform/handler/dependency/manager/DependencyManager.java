package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.platform.handler.dependency.Dependency;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.platform.registry.ServiceRegistry;
import br.ufpe.cin.dsoa.service.Service;


public class DependencyManager implements ServiceListener, NotificationListener {

	/**
	 * The managed dependency
	 */
	private Dependency dependency;
	//private DependencyManagerMBean dependencys
	
	/**
	 * The component responsible for service selection.
	 */
	private BundleContext context;
	private ServiceRegistry serviceRegistry;
	private ServiceTracker brokerTracker;
	
	/**
	 * Signals that it is waiting for a Broker in order to resolve 
	 * its dependency
	 */
	private Boolean waiting;
	
	/**
	 * The component responsible for analyzing "service contracts" 
	 */
	private Verifier verifier;
	private ServiceTracker verifierTracker;
	
	public DependencyManager(Dependency dependency) {
		this.dependency = dependency;
		this.context = dependency.getHandler().getInstanceManager().getContext();
		this.waiting = true;
		//this.brokerTracker = new ServiceTracker(dependency.getContext(), ServiceRegistry.class.getName(), new BrokerTrackerCustomizer());
		//this.verifierTracker = new ServiceTracker(dependency.getContext(), Verifier.class.getName(), new VerifierTrackerCustomizer());
	}

	public void start() {
		this.brokerTracker.open();
		this.verifierTracker.open();
	}

	public void stop() {
		brokerTracker.close();
		verifierTracker.close();
		release();
	}
	
	public void resolve() {
		if (serviceRegistry != null) {
			serviceRegistry.getBestService(dependency.getSpecification(), dependency.getBlackList(), this);
		} else {
			synchronized (waiting) {
				waiting = Boolean.TRUE;
			}
		}
	}

	public void release() {
		dependency.setService(null);
		dependency.computeDependencyState();
	}
	
	private void configureVerifierAgents(Service service) {
		this.verifier.configure(this, service.getServiceId(), dependency.getAttributeConstraintList());
	}
	
	public void onArrival(Service service) {
		configureVerifierAgents(service);
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
	
	class BrokerTrackerCustomizer implements ServiceTrackerCustomizer {
		public Object addingService(ServiceReference reference) {
			synchronized(DependencyManager.this) {
				if (serviceRegistry == null) {
					serviceRegistry = (ServiceRegistry) DependencyManager.this.context.getService(reference);
				}
				
				if (verifier != null) {
					if (waiting) {
						serviceRegistry.getBestService(dependency.getSpecification(), dependency.getBlackList(), DependencyManager.this);
						waiting = Boolean.FALSE;
					}
				}
				return serviceRegistry;
			}
		}

		public void modifiedService(ServiceReference reference, Object service) {
			// Just do nothing!
		}

		public void removedService(ServiceReference reference, Object service) {
			synchronized(DependencyManager.this) {
				DependencyManager.this.context.ungetService(reference);
				waiting = Boolean.TRUE;
				serviceRegistry = null;
			}
		}	
	}

	class VerifierTrackerCustomizer implements ServiceTrackerCustomizer {
		public Object addingService(ServiceReference reference) {
			synchronized(DependencyManager.this) {
				verifier = (Verifier) DependencyManager.this.context.getService(reference);
				if (serviceRegistry != null) {
					synchronized (waiting) {
						if (waiting) {
							serviceRegistry.getBestService(dependency.getSpecification(), dependency.getBlackList(), DependencyManager.this);
							waiting = Boolean.FALSE;
						}
					}
				}
				return verifier;
			}
		}

		public void modifiedService(ServiceReference reference, Object service) {
			// Just do nothing!
		}

		public void removedService(ServiceReference reference, Object service) {
			synchronized(DependencyManager.this) {
				DependencyManager.this.context.ungetService(reference);
				verifier = null;
			}
		}	
	}
	
	public void receive(Map result, Object userObject, String statementName) {
		// TODO Auto-generated method stub
		
	}

	public void receive(Object result, String statementName) {
		// TODO Auto-generated method stub
		
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
