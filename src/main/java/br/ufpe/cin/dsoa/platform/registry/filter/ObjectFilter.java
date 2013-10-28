package br.ufpe.cin.dsoa.platform.registry.filter;

import br.ufpe.cin.dsoa.api.service.Expression;

public class ObjectFilter extends FilterBuilder {

	private final String name;
	private final Object value;
	private final Expression expression;
	
	public ObjectFilter(String name, Expression expression, Object value) {
		super();
		this.name = name;
		this.value = value;
		this.expression = expression;
	}

	@Override
	public StringBuilder append(StringBuilder builder) {
		
		String renderedExpression = expression.renderExpression(this.name, value.toString());
		return builder.append('(').append(renderedExpression).append(')');
		
	}

}
