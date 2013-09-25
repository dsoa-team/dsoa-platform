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
	
	//private Planner planner;

	public DependencyManager(Dependency dependency) {
		this.dependency = dependency;
		this.dsoa = dependency.getHandler().getDsoaPlatform();
		this.analyzer = new DsoaAnalyzer(dsoa.getEpService(), dsoa.getAttEventMapperCatalog());
	}

	public void resolve() {
		dsoa.getServiceRegistry().getBestService(dependency.getSpecification(), dependency.getBlackList(), this);
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
		this.analyzer.start(service.getCompomentId(), dependency.getAttributeConstraintList(), this );
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
}
