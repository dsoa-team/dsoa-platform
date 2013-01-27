package br.ufpe.cin.dsoa.handlers.dependency;

import br.ufpe.cin.dsoa.contract.ServiceImpl;
import br.ufpe.cin.dsoa.osgi.ServiceModelFactory;

public class DependencyManager implements SelectionListener, ViolationListener {

	private ServiceDependency dependency;
	private SelectionStrategy selectionStrategy;
	private MonitoringStrategy monitoringStrategy;
	
	public static DependencyManager createManager(ServiceDependency dependency) {
		return new DependencyManager(dependency);
	}
	
	private DependencyManager(ServiceDependency dependency) {
		super();
		this.dependency = dependency;
		this.dependency.setDependencyManager(this);
	}

	public void resolve() {
		ServiceModel service = this.selectionStrategy.select(this, dependency.getMetadata());
		if (service != null) {
			notifySelection(service);
		}
	}
	
	public void notifySelection(ServiceModel serviceModel) {
		
		Object instrumentedService = this.monitoringStrategy.monitor(this, serviceModel, dependency.getMetadata());
		dependency.setService(serviceModel);
	}

	public void notifyViolation(Violation violation) {
		
	}

}
