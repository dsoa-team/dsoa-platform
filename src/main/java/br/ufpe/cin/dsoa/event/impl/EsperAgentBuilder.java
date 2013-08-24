package br.ufpe.cin.dsoa.event.impl;

import java.util.Iterator;
import java.util.List;

import com.espertech.esper.epl.generated.EsperEPL2GrammarParser.newAssign_return;
import com.sun.org.apache.regexp.internal.RESyntaxException;

import br.ufpe.cin.dsoa.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.agent.InputEvent;
import br.ufpe.cin.dsoa.agent.OutputEvent;
import br.ufpe.cin.dsoa.agent.ProcessingMapping;
import br.ufpe.cin.dsoa.event.Property;
import br.ufpe.cin.dsoa.util.Constants;

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

		this.in = ((ProcessingMapping) eventProcessingAgent.getProcessing()).getInputEvent();
		this.out = ((ProcessingMapping) eventProcessingAgent.getProcessing()).getOutputEvent();
	}

	public void buildSelectClause() {
		this.queryString.append(" INSERT INTO " + this.out.getType());
		this.queryString.append(" SELECT ");
		this.queryString.append(extractSelect(this.out.getMetadata(), "metadata") + ", ");
		this.queryString.append(extractSelect(this.out.getData(), "data"));
	}

	

	public void buildFromClause() {
		this.queryString.append(" FROM ");
		this.queryString.append(this.in.getType() + " as " + this.in.getAlias());

	}

	public void buildWhereClause() {}

	public void buildGroupByClause() {}

	public void buildHavingClause() {}

	public Query getQuery() {
		String id = this.eventProcessingAgent.getId();
		this.query = new Query(id, this.queryString.toString());

		return query;
	}
	
	private String extractSelect(List<Property> properties, String prefix) {
		StringBuilder result = new StringBuilder();
		Iterator<Property> iterator = properties.iterator();
		boolean first = true;
		while (iterator.hasNext()) {
			if (!first) {
				result.append(", ");
			}
			first = false;
			Property p = iterator.next();
			result.append(p.getExpression() + " metadata" + Constants.TOKEN + p.getId() + " ");
		}
		return result.toString();
	}

}
