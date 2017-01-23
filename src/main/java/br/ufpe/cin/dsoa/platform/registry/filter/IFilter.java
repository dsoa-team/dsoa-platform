package br.ufpe.cin.dsoa.platform.registry.filter;

import br.ufpe.cin.dsoa.api.service.RelationalOperator;



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
		
		String renderedExpression = RelationalOperator.EQ.renderExpression(this.name, value);
		return builder.append('(').append(renderedExpression).append(')');
	}

}
