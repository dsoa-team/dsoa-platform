package br.ufpe.cin.dsoa.api.event;

import br.ufpe.cin.dsoa.api.service.RelationalOperator;

public class FilterExpression {

	private Property property;
	private RelationalOperator expression;

	public FilterExpression(Property property, RelationalOperator expression) {
		super();
		this.property = property;
		this.expression = expression;
	}

	public Property getProperty() {
		return property;
	}

	public RelationalOperator getExpression() {
		return expression;
	}

	@Override
	public String toString() {
		return "FilterExpression [property=" + property + ", expression=" + expression + "]";
	}

}
