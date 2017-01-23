package br.ufpe.cin.dsoa.platform.attribute;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.Constraint;

public interface ConstraintViolationListener {
	public void constraintViolated(String serviceId, Constraint constraint,
			AttributeValue value);
}
