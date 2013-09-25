package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import java.util.List;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeNotificationListener;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;

public interface Analyzer {
	void start(String servicePid, List<AttributeConstraint> constraints,
			AttributeEventMapperCatalog attributeEventMapperCatalog, EventProcessingService eventProcessingService, AttributeNotificationListener listener);

	void stop();
}
