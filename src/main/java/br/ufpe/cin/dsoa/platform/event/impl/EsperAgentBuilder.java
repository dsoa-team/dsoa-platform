package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Iterator;
import java.util.List;

import br.ufpe.cin.dsoa.api.event.PropertyType;
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

	protected OutputEvent out;
	protected InputEvent in;

	public EsperAgentBuilder(EventProcessingAgent eventProcessingAgent) {
		if (!(eventProcessingAgent.getProcessing() instanceof ProcessingMapping)) {
			throw new IllegalArgumentException("Processing class not allowed");
		}
		this.eventProcessingAgent = eventProcessingAgent;
		this.queryString = new StringBuilder();

		this.in = ((ProcessingMapping) eventProcessingAgent.getProcessing()).getInputEvent();
		this.out = ((ProcessingMapping) eventProcessingAgent.getProcessing()).getOutputEvent();
	}

	public void buildContextClause() {
		if (this.in.getType() == "InvocationEvent") {
			this.queryString.append(" context " + Constants.CONTEXT_NAME);
		}
	}

	public void buildInsertIntoClause() {
		this.queryString.append(" INSERT INTO " + this.out.getType());
	}

	public void buildSelectClause() {
		this.queryString.append(" SELECT ");
		this.queryString.append(extractSelect(this.out.getMetadata(), "metadata") + ", ");
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
		this.queryString.append(String.format("(%s %s)", in.getWindow().getSize(), in.getWindow()
				.getUnit()));
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
				if(!first){
					queryString.append(" AND ");
					first = false;
				}
				String expression = filter.getExpression();
				String operator = RelationalOperator.valueOf(filter.getOperator()).getOperator();
				String value = filter.getValue();

				String clause = String.format("%s%s'%s'", parseExpression(expression, alias),
						operator, value);
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
					this.parseExpression(p.getExpression(), in.getAlias()), prefix,
					Constants.UNDERLINE, p.getName()));
		}
		return result.toString();
	}

	protected String parseExpression(String expression, String alias) {

		String parsedExpression = expression
				.replaceAll("\\" + Constants.TOKEN, Constants.UNDERLINE);
		parsedExpression = parsedExpression.replaceAll(alias + Constants.UNDERLINE, alias
				+ Constants.TOKEN);

		return parsedExpression;
	}

}
