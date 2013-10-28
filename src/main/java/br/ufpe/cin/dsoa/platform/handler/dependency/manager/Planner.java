package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;

public class Planner {

	private DependencyManager manager;

	public void setDependencyManager(DependencyManager manager) {
		this.manager = manager;
	}

	public void evaluate(AttributeConstraint constraint, AttributeValue value) {
		System.err.println(">>>> Trocar serviço <<<");
		manager.release();
		System.err.println(">>>> Handler Invalido <<<");
		manager.resolve();
		System.err.println(">>>> Trocou serviço <<<");
	}

}
