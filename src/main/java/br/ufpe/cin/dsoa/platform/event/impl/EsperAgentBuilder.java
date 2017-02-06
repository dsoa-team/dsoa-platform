package br.ufpe.cin.dsoa.platform.event.impl;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.agent.AgentList;
import br.ufpe.cin.dsoa.api.event.agent.Context;
import br.ufpe.cin.dsoa.api.event.agent.Element;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.event.agent.Filter;
import br.ufpe.cin.dsoa.api.event.agent.InputEvent;
import br.ufpe.cin.dsoa.api.event.agent.OutputEvent;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingMapping;
import br.ufpe.cin.dsoa.api.service.RelationalOperator;
import br.ufpe.cin.dsoa.util.Constants;

public class EsperAgentBuilder implements QueryBuilder {

	private EventProcessingAgent eventProcessingAgent;
	private Query query;
	protected StringBuilder queryString;
	protected StringBuilder contextString;
	
	protected OutputEvent out;
	protected InputEvent in;
	private Context context;


	public EsperAgentBuilder(EventProcessingAgent eventProcessingAgent) {
		if (!(eventProcessingAgent.getProcessing() instanceof ProcessingMapping)) {
			throw new IllegalArgumentException("Processing class not allowed");
		}
		this.eventProcessingAgent = eventProcessingAgent;
		this.queryString = new StringBuilder();
		this.contextString = new StringBuilder();

		ProcessingMapping agent = (ProcessingMapping) eventProcessingAgent
				.getProcessing();

		this.in = agent.getInputEvent();
		this.out = agent.getOutputEvent();
		this.context = agent.getContext();
	}

	public void buildContextClause() {
		if (this.context != null) {
			if (context.getElements() == null || context.getElements().size() == 0) {
				throw new IllegalArgumentException("Context " + context.getId() + " has no elements!");
			} 
			String contextName = (context.getId() != null ? context.getId() : eventProcessingAgent.getId());
			this.contextString.append("create context " + contextName + " partition by ");
			for (Element element : this.context.getElements()) {
				this.contextString.append(parseExpressionRemovingAlias(element.getId(), in.getAlias()) + ",");
			}
			int lastComma = contextString.lastIndexOf(",");
			this.contextString.replace(lastComma, lastComma + 1, " ");
			this.contextString.append(" from " + in.getType());
			this.queryString.append(" context " + contextName);
		} 
	}
	
	protected String parseExpressionRemovingAlias(String expression, String alias) {

		String parsedExpression = expression.replaceAll("\\" + Constants.TOKEN,
				Constants.UNDERLINE);
		parsedExpression = parsedExpression.replaceAll(alias
				+ Constants.UNDERLINE,"");

		return parsedExpression;
	}
	
	public String getContext() {
		return this.contextString.toString();
	}

	public void buildInsertIntoClause() {
		this.queryString.append(" INSERT INTO " + this.out.getType());
	}

	public void buildSelectClause() {
		this.queryString.append(" SELECT ");
		this.queryString.append(extractSelect(this.out.getMetadata(),
				"metadata") + ", ");
		this.queryString.append(extractSelect(this.out.getData(), "data"));
	}

	public void buildFromClause() {
		this.queryString.append(" FROM ");
		this.queryString.append(this.in.getType());
	}

	public void buildFilterClause() {
	}

	public void buildWindowClause() {
		this.queryString.append(".win:" + in.getWindow().getType());
		this.queryString.append(String.format("(%s %s)", in.getWindow()
				.getSize(), in.getWindow().getUnit()));
	}

	public void buildAliasClause() {
		this.queryString.append(" as " + this.in.getAlias());
	}

	public void buildWhereClause() {

		List<Filter> filters = this.in.getFilter();
		String alias = this.in.getAlias();

		boolean first = true;

		if (filters != null && !filters.isEmpty()) {
			this.queryString.append(" WHERE ");
			for (Filter filter : filters) {
				if (!first) {
					queryString.append(" AND ");
					first = false;
				}
				String expression = filter.getExpression();
				String operator = RelationalOperator.valueOf(
						filter.getOperator()).getOperator();
				String value = filter.getValue();

				String clause = String.format("%s%s\"%s\"",
						parseExpression(expression, alias), operator, value);
				queryString.append(clause);
			}
		}
	}

	public void buildGroupByClause() {
	}

	public void buildHavingClause() {
	}

	public Query getQuery() {
		String id = this.eventProcessingAgent.getId();
		this.query = new Query(id, this.queryString.toString());

		return query;
	}

	private String extractSelect(List<PropertyType> properties, String prefix) {
		StringBuilder result = new StringBuilder();
		Iterator<PropertyType> iterator = properties.iterator();
		boolean first = true;
		while (iterator.hasNext()) {
			if (!first) {
				result.append(", ");
			}
			first = false;
			PropertyType p = iterator.next();
			// empty string is: Constants.TOKEN
			result.append(String.format("%s as %s%s%s ",
					this.parseExpression(p.getExpression(), in.getAlias()),
					prefix, Constants.UNDERLINE, p.getName()));
		}
		return result.toString();
	}

	protected String parseExpression(String expression, String alias) {

		String parsedExpression = expression.replaceAll("\\" + Constants.TOKEN,
				Constants.UNDERLINE);
		parsedExpression = parsedExpression.replaceAll(alias
				+ Constants.UNDERLINE, alias + Constants.TOKEN);

		return parsedExpression;
	}

	/*
	 * TODO: Remove: Just a quick test...
	 */

	public static void main(String[] args) {
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><agents>");
		buf.append("<agent id=\"AvgResponseTimeAgent\">");
		buf.append("<description>Calculates avg response time attribute</description>");
		buf.append("<mapping id=\"AvgResponseTimeMapping\">");
		buf.append("<input-event type=\"ServiceMonitoringEvent\" alias=\"event\">");
		buf.append("<window type=\"time\" size=\"10\" unit=\"sec\"/>");
		buf.append("</input-event>");
		buf.append("<output-event type=\"AvgResponseTimeEvent\">");
		buf.append("<metadata>");
		buf.append("<property id=\"id\" expression=\"event.metadata.id\" />");
		buf.append("<property id=\"timestamp\" expression=\"event.metadata.timestamp\" />");
		buf.append("<property id=\"source\" expression=\"event.metadata.source\" />");
		buf.append("</metadata>");
		buf.append("<data>");
		buf.append("<property id=\"value\" type=\"java.lang.Double\" ");
		buf.append("expression=\"avg(event.data.lastResponseTime)\" />");
		buf.append("</data>");
		buf.append("</output-event>");
		buf.append("<context id=\"AvgResponseTimeCtx\" >");
		buf.append("<element id=\"event.data.serviceId\" />");
		buf.append("<element id=\"event.data.consumerId\" />");
		buf.append("<element id=\"event.data.operation\" />");
		buf.append("</context>");
		buf.append("</mapping>");
		buf.append("</agent>");
		buf.append("</agents>");
		System.out.println(buf.toString());
		handleAgentDefinitions(buf.toString());
	}

	private static void handleAgentDefinitions(String agent) {
		AgentList agentList;
		try {
			Unmarshaller u = createUnmarshaller(AgentList.class);
			StringReader reader = new StringReader(agent);
			agentList = (AgentList) u.unmarshal(reader);

			for (EventProcessingAgent eventProcessingAgent : agentList
					.getAgents()) {
				System.out.println(eventProcessingAgent.toString());
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private static Unmarshaller createUnmarshaller(Class<?> clazz)
			throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(clazz);
		return context.createUnmarshaller();
	}

}
