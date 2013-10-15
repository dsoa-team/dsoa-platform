package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.platform.handler.dependency.Dependency;

public class Planner {

	public void evaluate(Dependency dependency, AttributeConstraint constraint, AttributeValue value) {
		dependency.stop();
		dependency.start();
	}

}
