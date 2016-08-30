package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.attribute.AttributeNotificationListener;
import br.ufpe.cin.dsoa.platform.handler.dependency.Dependency;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.util.Constants;

public class DependencyManager implements ServiceListener, AttributeNotificationListener {

	/**
	 * The managed dependency
	 */
	private Dependency dependency;

	private Analyzer analyzer;

	private Monitor monitor;

	private Planner planner;

	public DependencyManager(Dependency dependency) {
		this.dependency = dependency;
	}

	private void initializeControlLoop() {
		this.monitor = new Monitor(this);
		this.analyzer = new Analyzer();
		this.planner = new Planner();
	}

	private void configureControlLoop() {
		// configure monitor
		this.monitor.instrument(dependency);

		// configure analyzer
		this.analyzer.setPlatform(getDsoaPlatform());

		// configure planner
		this.planner.setDependencyManager(this);
	}

	public DsoaPlatform getDsoaPlatform() {
		return dependency.getHandler().getDsoaPlatform();
	}
	
	public void start() {
		this.initializeControlLoop();
		this.configureControlLoop();
		this.resolve();
	}

	public void stop() {
		this.release();
	}

	public void resolve() {
		System.err.println("dependency state: " + dependency.isValid());
		dependency.getHandler().getDsoaPlatform().getServiceRegistry().getBestService(dependency.getSpecification(),
				dependency.getBlackList(), this);
	}

	public void release() {
		if (this.dependency != null) {
			synchronized (dependency) {
				
				if (dependency.isValid()) {
					this.dependency.setValid(false);
					
					this.notifyUnbind();
					this.dependency.getBlackList().clear();//TODO:REMOVE
					if (dependency.getService() != null) {
						this.dependency.getBlackList().add(dependency.getService().getProviderId());
						this.dependency.setService(null);
					}
				}
			}
		}
		this.analyzer.stop();
	}

	private void notifyUnbind() {
		Service service = dependency.getService();
		String serviceId = service.getProviderId();
		String consumerId = dependency.getComponentId();
		String serviceInterface = service.getSpecification().getServiceInterface();

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Constants.SERVICE_ID, serviceId);
		data.put(Constants.CONSUMER_ID, consumerId);
		data.put(Constants.SERVICE_INTERFACE, serviceInterface);
		
		this.notify(Constants.UNBIND_EVENT, data);
	}
	
	private void notifyBind() {
		Service service = dependency.getService();
		String serviceId = service.getProviderId();
		String consumerId = dependency.getComponentId();
		String serviceInterface = service.getSpecification().getServiceInterface();

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Constants.SERVICE_ID, serviceId);
		data.put(Constants.CONSUMER_ID, consumerId);
		data.put(Constants.SERVICE_INTERFACE, serviceInterface);
		
		this.notify(Constants.BIND_EVENT, data);
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
		this.notifyBind();
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

	private void notify(String eventTypeName, Map<String, Object> data) {
		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put(Constants.EVENT_SOURCE, dependency.getId());

		dependency.getHandler().getDsoaPlatform().getEventDistribuitionService().postEvent(eventTypeName, metadata, data);
		
	}
}
