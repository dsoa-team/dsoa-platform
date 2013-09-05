package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.EventConstants;

import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.FilterExpression;
import br.ufpe.cin.dsoa.api.event.Subscription;

public class EventListener {

	public static final String TOPIC_PREFIX = "br/ufpe/cin/dsoa/event/";
	private String topic;
	private BundleContext ctx;
	private EventConsumer consumer;
	protected Subscription subcription;

	public EventListener(BundleContext ctx, EventConsumer consumer, Subscription subscription) {
		this.ctx = ctx;
		this.consumer = consumer;
		this.topic = TOPIC_PREFIX + subscription.getEventTypeName();
		this.subcription = subscription;
	}
	
	private String buildFilter() {
		String filterStr = null;
		EventFilter filter = this.subcription.getFilter();
		List<FilterExpression> expList = (filter == null) ? null : filter.getFilterExpressions();
		if  (expList != null && !expList.isEmpty()) {
			LdapFilterAdapter ldap = new LdapFilterAdapter(ctx, expList);
			filterStr = ldap.toString();
		}
		return filterStr;
	}

	public void start() {
		Dictionary dict = new Hashtable();
		dict.put(EventConstants.EVENT_TOPIC, topic);
		dict.put(EventConstants.EVENT_FILTER, this.buildFilter());
	}
	
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
