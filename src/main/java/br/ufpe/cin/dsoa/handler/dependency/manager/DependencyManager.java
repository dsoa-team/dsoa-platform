package br.ufpe.cin.dsoa.handler.dependency.manager;

import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.broker.Broker;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.handler.dependency.Dependency;
import br.ufpe.cin.dsoa.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.ServiceProvider;


public class DependencyManager implements ServiceListener, NotificationListener {

	/**
	 * The managed dependency
	 */
	private Dependency dependency;
	//private DependencyManagerMBean dependencys
	
	/**
	 * The component responsible for service selection.
	 */
	private Broker broker;
	private ServiceReference brokerReference;
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
		this.waiting = true;
		this.brokerTracker = new ServiceTracker(dependency.getContext(), Broker.class.getName(), new BrokerTrackerCustomizer());
		this.verifierTracker = new ServiceTracker(dependency.getContext(), Verifier.class.getName(), new VerifierTrackerCustomizer());
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
		if (broker != null) {
			broker.getBestService(dependency.getContext(), dependency.getSpecificationName(), dependency.getGoalList(),
				dependency.getBlackList(), this);
		} else {
			synchronized (waiting) {
				waiting = Boolean.TRUE;
			}
		}
	}

	public void release() {
		dependency.setServiceProvider(null);
		dependency.computeDependencyState();
	}
	
	private void configureVerifierAgents(ServiceProvider provider) {
		this.verifier.configure(this, provider.getServicePid(), dependency.getGoalList());
	}
	
	public void onArrival(ServiceProvider provider) {
		configureVerifierAgents(provider);
		dependency.setServiceProvider(provider);
		dependency.computeDependencyState();
	}

	public void onDeparture(ServiceProvider provider) {
		release();
		resolve();
	}
	
	class BrokerTrackerCustomizer implements ServiceTrackerCustomizer {
		public Object addingService(ServiceReference reference) {
			if (broker == null) {
				broker = (Broker) dependency.getContext().getService(reference);
			}
			
			if (verifier != null) {
				synchronized (waiting) {
					if (waiting) {
						broker.getBestService(dependency.getContext(), dependency.getSpecificationName(), dependency.getGoalList(),
							dependency.getBlackList(), DependencyManager.this);
						waiting = Boolean.FALSE;
					}
				}
			}
			return broker;
		}

		public void modifiedService(ServiceReference reference, Object service) {
			// Just do nothing!
		}

		public void removedService(ServiceReference reference, Object service) {
			dependency.getContext().ungetService(reference);
			waiting = Boolean.TRUE;
			broker = null;
		}	
	}

	class VerifierTrackerCustomizer implements ServiceTrackerCustomizer {
		public Object addingService(ServiceReference reference) {
			verifier = (Verifier) dependency.getContext().getService(reference);
			if (broker != null) {
				synchronized (waiting) {
					if (waiting) {
						broker.getBestService(dependency.getContext(), dependency.getSpecificationName(), dependency.getGoalList(),
							dependency.getBlackList(), DependencyManager.this);
						waiting = Boolean.FALSE;
					}
				}
			}
			return verifier;
		}

		public void modifiedService(ServiceReference reference, Object service) {
			// Just do nothing!
		}

		public void removedService(ServiceReference reference, Object service) {
			dependency.getContext().ungetService(reference);
			verifier = null;
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

	public List<Goal> getGoalList() {
		return goalList;
	}
	
	public boolean addGoal(Goal goal) {
		return this.goalList.add(goal);
	}
	
	public boolean removeGoal(Goal goal) {
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
