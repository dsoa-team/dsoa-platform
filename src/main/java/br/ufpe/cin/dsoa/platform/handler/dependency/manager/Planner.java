package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;

public class Planner {

	private DependencyManager manager;

	public void setDependencyManager(DependencyManager manager) {
		this.manager = manager;
	}

	public void evaluate(String serviceId, AttributeConstraint constraint, AttributeValue value) {
		System.err.println("====================================================");
		System.err.println("ServiceId: " + serviceId);
		String op1 = constraint.getAttributeId();
		if (constraint.getOperation() != null) {
			op1 = "." + constraint.getOperation();
		}
		String expected = constraint.getExpression().renderExpression(op1,
				constraint.getThreashold() + "");
		System.err.println(String.format("Expected value= %s :: Monitored Value= %s", expected,
				value.getValue()));

		System.err.println("====================================================");
		manager.release();
		manager.resolve();
	}

}
