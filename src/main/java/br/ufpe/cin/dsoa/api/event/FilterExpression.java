package br.ufpe.cin.dsoa.api.event;

import br.ufpe.cin.dsoa.api.service.Expression;

public class FilterExpression {

	private Property property;
	private Expression expression;

	public FilterExpression(Property property, Expression expression) {
		super();
		this.property = property;
		this.expression = expression;
	}

	public Property getProperty() {
		return property;
	}

	public Expression getExpression() {
		return expression;
	}

}
