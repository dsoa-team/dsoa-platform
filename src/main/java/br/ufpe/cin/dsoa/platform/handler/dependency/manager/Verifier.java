package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import java.util.List;

import br.ufpe.cin.dsoa.api.event.NotificationListener;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;

public interface Verifier {
	void configure(NotificationListener listener, String servicePid, List<AttributeConstraint> constraints);
}
