package br.ufpe.cin.dsoa.platform.event;

import java.util.Map;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventType;

/**
 * - Transparencia do mecanismo de transporte de eventos - Contém adaptadores
 * responsáveis por transformar dados externos no modelo de eventos da
 * plataforma
 * 
 */
public interface EventDistribuitionService {

	public void postEvent(String eventTypeName, Map<String, Object> metadata,
			Map<String, Object> data);

	public void postEvent(Event event);

	public void subscribe(EventConsumer consumer, EventType eventType);

	// retorna id da subscription
	// public String subscribe(EventConsumer consumer, Subscription
	// subscription);

	// public void unsubscribe(String subscriptionId);
}