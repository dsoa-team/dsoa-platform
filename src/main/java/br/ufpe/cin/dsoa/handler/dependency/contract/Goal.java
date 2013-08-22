package br.ufpe.cin.dsoa.handler.dependency.contract;

public class Goal {
	private String attributeId;
	private String operation;
	private Expression expression;
	private double threashold;
	private long weight;

	public Goal(String attributeId, String operation, Expression expression, double threashold, WindowType type, long size, long wgt) {
		super();
		this.attributeId = attributeId;
		this.operation = operation;
		this.expression = expression;
		this.threashold = threashold;
		this.weight = wgt;
	}

	public String getAttributeId() {
		return attributeId;
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
		return "Goal [attributeId=" + this.attributeId + ", operation=" + this.operation + ", expression=" + this.expression + ", threashold=" + this.threashold
				+ ", weight=" + weight + "]";
	}

}
