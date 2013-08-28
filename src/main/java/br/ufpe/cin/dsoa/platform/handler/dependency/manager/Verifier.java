package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import java.util.List;

import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.service.AttributeConstraint;

public interface Verifier {
	void configure(NotificationListener listener, String servicePid, List<AttributeConstraint> constraints);
}
