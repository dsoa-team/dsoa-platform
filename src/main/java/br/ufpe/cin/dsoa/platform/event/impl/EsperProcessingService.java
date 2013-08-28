package br.ufpe.cin.dsoa.platform.event.impl;

import br.ufpe.cin.dsoa.event.Event;
import br.ufpe.cin.dsoa.event.Subscription;
import br.ufpe.cin.dsoa.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

/**
 * Esper implementation of EventProcessingService. This component implements DSOA event 
 * processing capability. It receives events from Event Producers through Event Channels. 
 * These events comprise streams that are connected through Event Processing Agents. 
 * These components filter, aggregate, and transform events generating new Event Streams
 * that can be forwarded to Event Consumers that subscribe themselves to listen those events.
 * The collection of Event Producers, Event Processing Agents, and Event Consumers 
 * connected together compose Event Processing Networks.
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
