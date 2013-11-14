package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.attribute.AttributeNotificationListener;
import br.ufpe.cin.dsoa.platform.handler.dependency.Dependency;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;

public class DependencyManager implements ServiceListener, AttributeNotificationListener {

	/**
	 * The managed dependency
	 */
	private Dependency dependency;

	private DsoaPlatform dsoa;

	private Analyzer analyzer;

	private Monitor monitor;

	private Planner planner;

	public DependencyManager(Dependency dependency) {
		this.dependency = dependency;
		this.dsoa = dependency.getHandler().getDsoaPlatform();

		this.initializeControlLoop();
	}

	private void initializeControlLoop() {
		this.monitor = new Monitor(this.dsoa);
		this.analyzer = new Analyzer();
		this.planner = new Planner();
	}

	private void configureControlLoop() {
		// configure monitor
		this.monitor.instrument(dependency);

		// configure analyzer
		this.analyzer.setPlatform(dsoa);

		// configure planner
		this.planner.setDependencyManager(this);
	}

	public void start() {
		this.configureControlLoop();
		this.resolve();
	}

	public void stop() {
		this.release();
	}

	public void resolve() {
		System.err.println("dependency state: " + dependency.isValid());
		dsoa.getServiceRegistry().getBestService(dependency.getSpecification(),
				dependency.getBlackList(), this);
	}

	public void release() {
		if (this.dependency != null) {
			synchronized (dependency) {
				if (dependency.isValid()) {
					this.dependency.setValid(false);
					this.dependency.getBlackList().clear();
					if (dependency.getService() != null) {
						this.dependency.getBlackList().add(dependency.getService().getProviderId());
						this.dependency.setService(null);
					}
				}
			}
		}
		this.analyzer.stop();
	}

	public String getServiceInterface() {
		return this.dependency.getSpecification().getServiceInterface();
	}

	public void onArrival(Service service) {
		this.analyzer
				.start(dependency.getComponentId(), service.getProviderId(), dependency
						.getSpecification().getNonFunctionalSpecification()
						.getAttributeConstraints(), this);

		this.dependency.setService(service);
		this.dependency.setValid(true);
	}

	public void onDeparture(Service service) {
		this.release();
	}

	public void onError(Exception e) {
	}

	@Override
	public void handleNotification(String serviceId, AttributeConstraint constraint,
			AttributeValue value) {
		synchronized (dependency) {
			this.planner.evaluate(serviceId, constraint, value);
		}
	}
}
