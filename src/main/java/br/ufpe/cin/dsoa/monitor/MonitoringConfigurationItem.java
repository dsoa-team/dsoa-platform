package br.ufpe.cin.dsoa.monitor;


public class MonitoringConfigurationItem {

	private final String operation;
	private final String attribute;
	private final String expression;
	private final double value;
	private final String statistic;
	private final double windowValue;
	private final String windowUnit;
	
	public MonitoringConfigurationItem(String operation, String attribute,
			String expression, double value, String statistic,
			String windowUnit, double windowValue, MonitoringConfiguration config) {

		this.operation = operation;
		this.attribute = attribute;
		this.expression = expression;
		this.value = value;
		this.windowUnit = windowUnit;
		this.windowValue = windowValue;
		this.statistic = statistic;
	}

	public String getOperation() {
		return operation;
	}

	public String getAttribute() {
		return attribute;
	}

	public String getExpression() {
		return expression;
	}

	public double getValue() {
		return value;
	}

	public String getStatistic() {
		return statistic;
	}

	public double getWindowValue() {
		return windowValue;
	}

	public String getWindowUnit() {
		return windowUnit;
	}

	
}
