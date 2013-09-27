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
import br.ufpe.cin.dsoa.platform.monitor.DynamicProxyFactory;
import br.ufpe.cin.dsoa.util.Constants;

public class DependencyManager implements ServiceListener, AttributeNotificationListener {

	/**
	 * The managed dependency
	 */
	private Dependency dependency;

	private DsoaPlatform dsoa;

	private Analyzer analyzer;

	// private Planner planner;

	public DependencyManager(Dependency dependency) {
		this.dependency = dependency;
		this.dsoa = dependency.getHandler().getDsoaPlatform();
		this.dependency.setDynamicProxy(createDependencyProxy());
		this.analyzer = new DsoaAnalyzer(dsoa.getEpService(), dsoa.getAttEventMapperCatalog());
	}

	private DynamicProxyFactory createDependencyProxy() {
		EventType invocationEvent = dsoa.getEventTypeCatalog().get(Constants.INVOCATION_EVENT);
		EventAdmin eventAdmin = this.dsoa.getEventDistribuitionService();
		DynamicProxyFactory dynamicProxy = new DynamicProxyFactory(dependency, eventAdmin,
				invocationEvent);

		return dynamicProxy;
	}

	public void resolve() {
		dsoa.getServiceRegistry().getBestService(dependency.getSpecification(),
				dependency.getBlackList(), this);
	}

	public void release() {
		this.analyzer.stop();
		dependency.setService(null);
		dependency.computeDependencyState();
	}

	public String getServiceInterface() {
		return dependency.getSpecification().getServiceInterface();
	}

	public void onArrival(Service service) {
		this.analyzer
				.start(service.getCompomentId(), dependency.getAttributeConstraintList(), this);
		dependency.setService(service);
		dependency.computeDependencyState();
	}

	public void onDeparture(Service service) {
		release();
		resolve();
	}

	public void onError(Exception e) {
	}

	@Override
	public void handleNotification(AttributeConstraint constraint, AttributeValue value) {

		System.out.println("Constraint: " + constraint);
		System.out.println("Value: " + value);
	}
}
