package br.ufpe.cin.dsoa.event.subscription;

import java.util.ArrayList;
import java.util.List;

public class EventFilter {

	private List<FilterExpression> filterExpressions;

	public EventFilter(List<FilterExpression> filterExpressions) {
		super();
		this.filterExpressions = new ArrayList<FilterExpression>(filterExpressions);
	}

	public List<FilterExpression> getFilterExpressions() {
		return new ArrayList<FilterExpression>(filterExpressions);
	}
}
