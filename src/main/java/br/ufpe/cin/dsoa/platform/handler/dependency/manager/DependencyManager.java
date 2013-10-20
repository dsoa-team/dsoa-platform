package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import org.osgi.service.event.EventAdmin;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.event.EventType;
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
		this.monitor = new Monitor();
		this.analyzer = new Analyzer();
		this.planner = new Planner();
	}
	
	private void configureControlLoop() {
		// configure monitor
		EventType invocationEvent = this.dsoa.getEventTypeCatalog().get(Constants.INVOCATION_EVENT);
		EventAdmin eventAdmin = this.dsoa.getEventDistribuitionService();

		this.monitor.setEventAdmin(eventAdmin);
		this.monitor.setEventType(invocationEvent);
		this.monitor.instrument(dependency);

		// configure analyzer
		this.analyzer.setPlatform(dsoa);
	}
	
	public void start() {
		this.configureControlLoop();
		this.resolve();
	}

	public void stop() {
		this.analyzer.stop();
		this.release();
	}

	public void resolve() {
		dsoa.getServiceRegistry().getBestService(dependency.getSpecification(), dependency.getBlackList(), this);
	}

	public void release() {
		this.analyzer.stop();
		this.dependency.setValid(false);
		this.dependency.setService(null);
	}

	public String getServiceInterface() {
		return this.dependency.getSpecification().getServiceInterface();
	}

	public void onArrival(Service service) {
		this.analyzer.start(service.getCompomentId(), dependency.getSpecification().getNonFunctionalSpecification()
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
	public void handleNotification(AttributeConstraint constraint, AttributeValue value) {
		this.planner.evaluate(constraint, value);
	}
}
