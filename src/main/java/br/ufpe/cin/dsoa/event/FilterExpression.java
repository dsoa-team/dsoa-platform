package br.ufpe.cin.dsoa.event;

public class FilterExpression {

	private Parameter parameter;
	private Expression expression;

	public FilterExpression(Parameter parameter, Expression expression) {
		super();
		this.parameter = parameter;
		this.expression = expression;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public Expression getExpression() {
		return expression;
	}

}
