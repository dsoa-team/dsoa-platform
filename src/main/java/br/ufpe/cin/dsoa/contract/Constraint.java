package br.ufpe.cin.dsoa.contract;

public class Constraint {
	private String metric;
	private String operation;
	private Expression expression;
	private double threashold;
	private long weight;

	public Constraint(String metric, String operation, Expression expression, double threashold, long weight) {
		super();
		this.metric = metric;
		this.operation = operation;
		this.expression = expression;
		this.threashold = threashold;
		this.weight = weight;
	}

	public String getMetric() {
		return metric;
	}

	public String getOperation() {
		return operation;
	}

	public Expression getExpression() {
		return expression;
	}

	public double getThreashold() {
		return threashold;
	}

	public long getWeight() {
		return weight;
	}

	

}
