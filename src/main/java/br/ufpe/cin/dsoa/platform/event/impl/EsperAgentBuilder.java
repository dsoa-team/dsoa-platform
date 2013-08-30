package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Iterator;
import java.util.List;

import br.ufpe.cin.dsoa.event.agent.meta.EventProcessingAgent;
import br.ufpe.cin.dsoa.event.agent.meta.InputEvent;
import br.ufpe.cin.dsoa.event.agent.meta.OutputEvent;
import br.ufpe.cin.dsoa.event.agent.meta.ProcessingMapping;
import br.ufpe.cin.dsoa.event.meta.Property;

public class EsperAgentBuilder implements QueryBuilder {

	private EventProcessingAgent eventProcessingAgent;
	private Query query;
	private StringBuilder queryString;

	private OutputEvent out;
	private InputEvent in;

	public EsperAgentBuilder(EventProcessingAgent eventProcessingAgent) {
		if (!(eventProcessingAgent.getProcessing() instanceof ProcessingMapping)) {
			throw new IllegalArgumentException("Processing class not allowed");
		}
		this.eventProcessingAgent = eventProcessingAgent;
		this.queryString = new StringBuilder();

		this.in = ((ProcessingMapping) eventProcessingAgent.getProcessing())
				.getInputEvent();
		this.out = ((ProcessingMapping) eventProcessingAgent.getProcessing())
				.getOutputEvent();
	}

	public void buildSelectClause() {
		this.queryString.append(" INSERT INTO " + this.out.getType());
		this.queryString.append(" SELECT ");
		this.queryString.append(extractSelect(this.out.getMetadata(),
				"metadata") + ", ");
		this.queryString.append(extractSelect(this.out.getData(), "data"));
	}

	public void buildFromClause() {
		this.queryString.append(" FROM ");
		this.queryString
				.append(this.in.getType() + " as " + this.in.getAlias());

	}

	public void buildWhereClause() {
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

	private String extractSelect(List<Property> properties, String prefix) {
		String alias = in.getAlias();
		StringBuilder result = new StringBuilder();
		Iterator<Property> iterator = properties.iterator();
		boolean first = true;
		while (iterator.hasNext()) {
			if (!first) {
				result.append(", ");
			}
			first = false;
			Property p = iterator.next();
			// empty string is: Constants.TOKEN
			result.append(String
					.format("%s as %s%s%s ", p.getPropertyType()
							.getExpression(), alias, "", p.getPropertyType()
							.getName()));
		}
		return result.toString();
	}

}
