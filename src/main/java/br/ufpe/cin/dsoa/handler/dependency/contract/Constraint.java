package br.ufpe.cin.dsoa.handler.dependency.contract;

public class Constraint {
	private String metric;
	private String operation;
	private WindowType windowType;
	private long windowSize;
	private Expression expression;
	private double threashold;
	private long weight;

	public Constraint(String metric, String operation, Expression expression, double threashold, WindowType type, long size, long wgt) {
		super();
		this.metric = metric;
		this.operation = operation;
		this.expression = expression;
		this.threashold = threashold;
		this.weight = wgt;
		this.windowType = type;
		this.windowSize = size;
	}

	public WindowType getWindowType() {
		return windowType;
	}

	public long getWindowSize() {
		return windowSize;
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

	@Override
	public String toString() {
		return "Constraint [metric=" + metric + ", operation=" + operation + ", windowType=" + windowType
				+ ", windowSize=" + windowSize + ", expression=" + expression + ", threashold=" + threashold
				+ ", weight=" + weight + "]";
	}

}
