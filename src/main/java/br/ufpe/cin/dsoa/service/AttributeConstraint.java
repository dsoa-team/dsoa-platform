package br.ufpe.cin.dsoa.service;


public class AttributeConstraint {
	private String attributeId;
	private String operation;
	private Expression expression;
	private double threashold;
	private long weight;

	public AttributeConstraint(String operation, String attributeId, Expression expression, double threashold, long wgt) {
		this(operation, attributeId,expression,threashold);	
		this.weight = wgt;
	}
	
	public AttributeConstraint(String operation, String attributeId, Expression expression, double threashold) {
		this(attributeId,expression,threashold);	
		this.operation = operation;
	}
	
	public AttributeConstraint(String attributeId, Expression expression, double threashold, long wgt) {
		this(attributeId,expression,threashold);	
		this.weight = wgt;
	}
	
	public AttributeConstraint(String attributeId, Expression expression, double threashold) {
		super();
		this.attributeId = attributeId;
		this.expression = expression;
		this.threashold = threashold;
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
		return "AttributeConstraint [attributeId=" + attributeId + ", operation=" + operation + ", expression="
				+ expression + ", threashold=" + threashold + ", weight=" + weight + "]";
	}

}
