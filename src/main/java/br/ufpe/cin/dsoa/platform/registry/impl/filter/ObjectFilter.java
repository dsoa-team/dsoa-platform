package br.ufpe.cin.dsoa.platform.registry.impl.filter;

import br.ufpe.cin.dsoa.api.service.RelationalOperator;

public class ObjectFilter extends FilterBuilder {

	private final String name;
	private final Object value;
	private final RelationalOperator expression;
	
	public ObjectFilter(String name, RelationalOperator expression, Object value) {
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
