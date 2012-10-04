package br.ufpe.cin.dsoa.contract;

public class Slo {
	private String operation;
	private String attribute;
	private Expression expression;
	private double value;
	private String statistic;
	private long weight;
	private double windowValue;
	private String windowUnit;

	public Slo(String attribute, Expression expression, double value,
			String operation, String statistic, long weight,
			double windowValue, String windowUnit) {
		this(attribute, value, operation, statistic);
		this.weight = weight;
		this.expression = expression;
		this.windowValue = windowValue;
		this.windowUnit = windowUnit;
	}

	public Slo(String attribute, double value, String operation,
			String statistic) {
		super();
		this.attribute = attribute;
		this.value = value;
		this.operation = operation;
		this.statistic = statistic;
	}

	public double getWindowValue() {
		return windowValue;
	}

	public String getWindowUnit() {
		return windowUnit;
	}

	public String getAttribute() {
		return attribute;
	}

	public Expression getExpression() {
		return expression;
	}

	public double getValue() {
		return value;
	}

	public String getOperation() {
		return operation;
	}

	public String getStatistic() {
		return statistic;
	}

	public long getWeight() {
		return weight;
	}

}
