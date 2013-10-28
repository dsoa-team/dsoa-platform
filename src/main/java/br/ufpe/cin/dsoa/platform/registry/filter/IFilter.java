package br.ufpe.cin.dsoa.platform.registry.filter;

import br.ufpe.cin.dsoa.api.service.Expression;



public class IFilter extends FilterBuilder {
	
	private final String name;
	private final String value;
	
	public IFilter(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	@Override
	public StringBuilder append(StringBuilder builder) {
		
		String renderedExpression = Expression.EQ.renderExpression(this.name, value);
		return builder.append('(').append(renderedExpression).append(')');
	}

}
