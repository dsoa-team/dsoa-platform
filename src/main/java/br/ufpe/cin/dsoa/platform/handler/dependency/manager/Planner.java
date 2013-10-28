package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;

public class Planner {

	private DependencyManager manager;

	public void setDependencyManager(DependencyManager manager) {
		this.manager = manager;
	}

	public void evaluate(AttributeConstraint constraint, AttributeValue value) {
		manager.release();
		manager.resolve();
	}

}
