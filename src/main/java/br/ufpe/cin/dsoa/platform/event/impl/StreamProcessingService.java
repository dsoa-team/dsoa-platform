package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.concurrent.ConcurrentHashMap;

import br.ufpe.cin.dsoa.event.EventConsumer;
import br.ufpe.cin.dsoa.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.event.agent.ProcessingMapping;
import br.ufpe.cin.dsoa.event.agent.ProcessingQuery;

public abstract class StreamProcessingService {

	private ConcurrentHashMap<String, EventConsumer> consumers;
	private ConcurrentHashMap<String, EventProcessingAgent> agents;

	public StreamProcessingService() {
		this.consumers = new ConcurrentHashMap<String, EventConsumer>();
		this.agents = new ConcurrentHashMap<String, EventProcessingAgent>();
	}
	
	public boolean registerConsumer(EventConsumer consumer) {
		return this.register(consumer.getId(), consumer, this.consumers);
	}

	public boolean registerAgent(EventProcessingAgent agent) {
		String id = null;
		String queryString = null;
		Query query = null;

		if (agent.getProcessing() instanceof ProcessingMapping) {
			QueryBuilder builder = this.getQueryBuilder(agent);
			QueryDirector director = new QueryDirector(builder);
			director.construct();
			query = director.getQuery();
		} else if (agent.getProcessing() instanceof ProcessingQuery) {
			id = agent.getId();
			queryString = ((ProcessingQuery) agent.getProcessing()).getQuery();
			query = new Query(id, queryString);
		}

		boolean added = this.register(agent.getId(), agent, this.agents);

		if (added) {
			this.startQuery(query);
		}

		return added;
	}

	private <T> boolean register(String subjectId, T subject, ConcurrentHashMap<String, T> map) {
		T stored = map.putIfAbsent(subjectId, subject);
		return (null == stored) ? true : false;
	}

	protected abstract QueryBuilder getQueryBuilder(EventProcessingAgent agent);

	protected abstract void startQuery(Query query);
}