package br.ufpe.cin.dsoa.platform.registry.filter;

import br.ufpe.cin.dsoa.api.service.Expression;



public class DFilter extends FilterBuilder{
	
	private final String name;
	private final double value;
	private final Expression expression;
	
	public DFilter(String name, Expression expression, double value) {
		super();
		this.name = name;
		this.value = value;
		this.expression = expression;
	}

	@Override
	public StringBuilder append(StringBuilder builder) {
		String renderedExpression = expression.renderExpression(this.name, value+"");

		return builder.append('(').append(renderedExpression).append(')');
	}
}
