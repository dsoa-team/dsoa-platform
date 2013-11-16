package br.ufpe.cin.dsoa.platform.event;

import java.util.Map;

public interface EventDistribuitionService {

	public void postEvent(String eventTypeName, Map<String, Object> metadata, Map<String, Object> data);
}
