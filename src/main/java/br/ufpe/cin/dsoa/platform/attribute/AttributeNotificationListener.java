package br.ufpe.cin.dsoa.platform.attribute;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;

public interface AttributeNotificationListener {
	public void handleNotification(AttributeConstraint constraint, AttributeValue value);
}
