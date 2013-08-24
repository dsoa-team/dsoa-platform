package br.ufpe.cin.dsoa.event.impl;

import java.util.concurrent.ConcurrentHashMap;

import br.ufpe.cin.dsoa.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.event.Event;
import br.ufpe.cin.dsoa.event.EventConsumer;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.event.Subscription;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

/**
 * Esper implementation of EventProcessingService
 * 
 * @author fabions
 * 
 */
public class EsperProcessingService extends StreamProcessingService implements EventProcessingService {

	private EPServiceProvider epServiceProvider;

	public void start() {
		this.epServiceProvider = EPServiceProviderManager.getProvider("EngineInstance", new Configuration());
	}

	public void stop() {
		this.epServiceProvider.destroy();
	}

	public void subscribe(String consumerId, Subscription subscription) {

	}

	public void publish(Event event) {

	}

	public boolean unRegisterConsumer(String consumerId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unRegisterAgent(String agentId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public QueryBuilder getQueryBuilder(EventProcessingAgent agent) {
		return new EsperAgentBuilder(agent);
	}

	@Override
	public void startQuery(Query query) {
		this.epServiceProvider.getEPAdministrator().createEPL(query.getQueryString(), query.getId());
	}

}
