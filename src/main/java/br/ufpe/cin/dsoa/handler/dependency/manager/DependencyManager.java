package br.ufpe.cin.dsoa.handler.dependency.manager;

import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.broker.Broker;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.handler.dependency.Dependency;
import br.ufpe.cin.dsoa.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.Constraint;
import br.ufpe.cin.dsoa.handler.dependency.contract.ServiceProvider;

public class DependencyManager implements NotificationListener {

	/**
	 * The managed dependency
	 */
	private Dependency dependency;
	
	private VerifierFactory verifierFactory;
	
	/**
	 * Listens service arrivals and departures notified by the Broker
	 */
	private ServiceListener listener;
	
	/**
	 * The component responsible for service selection.
	 */
	private Broker broker;
	
	/**
	 * Signals that it is waiting for a Broker in order to resolve 
	 * its dependency
	 */
	private Boolean waiting;

	private ServiceTracker brokerTracker;

	public DependencyManager(Dependency dependency) {
		this.dependency = dependency;
		this.waiting = false;
		this.listener = new ServiceListenerImpl();
		this.brokerTracker = new ServiceTracker(dependency.getContext(), Broker.class.getName(), new BrokerTrackerCustomizer());
	}

	public void start() {
		brokerTracker.open();
		resolve();
	}

	public void stop() {
		brokerTracker.close();
		release();
	}
	
	public void resolve() {
		if (broker != null) {
			broker.getBestService(dependency.getContext(), dependency.getSpecificationName(), dependency.getConstraintList(),
				dependency.getBlackList(), listener);
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
	
	public void configureVerifierAgents(ServiceProvider provider) {
		this.verifierFactory.configure(this, dependency.getConsumer().getId(), provider.getServicePid(), dependency.getConstraintList());
	}
	
	class ServiceListenerImpl implements ServiceListener {
		@Override
		public void onArrival(ServiceProvider provider) {
			configureVerifierAgents(provider);
			dependency.setServiceProvider(provider);
			dependency.computeDependencyState();
		}

		@Override
		public void onDeparture(ServiceProvider provider) {
			release();
			resolve();
		}
	}
	
	class BrokerTrackerCustomizer implements ServiceTrackerCustomizer {
		@Override
		public Object addingService(ServiceReference reference) {
			broker = (Broker) dependency.getContext().getService(reference);
			synchronized (waiting) {
				if (waiting) {
					broker.getBestService(dependency.getContext(), dependency.getSpecificationName(), dependency.getConstraintList(),
						dependency.getBlackList(), listener);
					waiting = Boolean.FALSE;
				}
			}
			return broker;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {
			// Just do nothing!
		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			broker = null;
		}	
	}

	@Override
	public void receive(Map result, Object userObject, String statementName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receive(Object result, String statementName) {
		// TODO Auto-generated method stub
		
	}
	
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
