package br.ufpe.cin.dsoa.platform.registry.filter;

import br.ufpe.cin.dsoa.api.service.RelationalOperator;



public class DFilter extends FilterBuilder{
	
	private final String name;
	private final double value;
	private final RelationalOperator expression;
	
	public DFilter(String name, RelationalOperator expression, double value) {
		super();
		this.name = name;
		this.value = value;
		this.expression = expression;
	}

	@Override
	public StringBuilder append(StringBuilder builder) {
		String renderedExpression = expression.renderExpression(this.name, new Double(value).toString());

		return builder.append('(').append(renderedExpression).append(')');
	}
}
