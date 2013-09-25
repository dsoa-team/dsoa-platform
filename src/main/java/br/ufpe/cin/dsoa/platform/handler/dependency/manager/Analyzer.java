package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import java.util.List;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.platform.attribute.AttributeNotificationListener;

public interface Analyzer {
	void start(String servicePid, List<AttributeConstraint> constraints,
			AttributeNotificationListener listener);

	void stop();
}
