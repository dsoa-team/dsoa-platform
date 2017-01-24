package br.ufpe.cin.dsoa.api.service.impl;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.RelationalOperator;


public class ConstraintImpl implements Constraint {
	
	public static final double WEIGHT_UNSET = -1;

	/** Metric id */
	private String attributeId;
	
	/** Relational operator */
	private RelationalOperator expression;
	
	//INCLUDE UNIT: The main problem is that I will not be able to use LDP to make comparations
	
	/** value */
	private double threashold;
	
	/** Operation name*/
	private String operation;

	private double weight;
	
	public ConstraintImpl(String attributeId, String operation, RelationalOperator expression, double threashold, double wgt) {
		this(attributeId,expression,threashold);	
		this.operation = operation;
		this.weight = wgt;
	}
	
	public ConstraintImpl(String attributeId, RelationalOperator expression, double threashold) {
		this.attributeId = attributeId;
		this.expression = expression;
		this.threashold = threashold;
	}
	
	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.api.service.impl.Constraint#getAttributeId()
	 */
	@Override
	public String getAttributeId() {
		return attributeId;
	}

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.api.service.impl.Constraint#getOperation()
	 */
	@Override
	public String getOperation() {
		return operation;
	}

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.api.service.impl.Constraint#getExpression()
	 */
	@Override
	public RelationalOperator getExpression() {
		return expression;
	}

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.api.service.impl.Constraint#getThreashold()
	 */
	@Override
	public double getThreashold() {
		return threashold;
	}


	@Override
	public double getWeight() {
		return weight;
	}
	
	@Override
	public String toString() {
		return "AttributeConstraint [attributeId=" + attributeId + ", operation=" + operation + ", expression="
				+ expression + ", threashold=" + threashold + "]";
	}
	
	
	/* (non-Javadoc)
	 * (constraint.operation | constraint.service).<attribute-name>.(<operation-name>)?.<relational-operator>
	 * @see br.ufpe.cin.dsoa.api.service.impl.Constraint#format()
	 */
	@Override
	public String format() {
		StringBuffer buffer = new StringBuffer();
		buffer.append((getOperation() != null) ? Attribute.OPERATION_CONSTRAINT : Attribute.SERVICE_CONSTRAINT);
		buffer.append(br.ufpe.cin.dsoa.util.Constants.TOKEN);
		buffer.append(getAttributeId());
		buffer.append(br.ufpe.cin.dsoa.util.Constants.TOKEN);
		buffer.append((getOperation() != null) ? getOperation() + br.ufpe.cin.dsoa.util.Constants.TOKEN : "");
		buffer.append(getExpression().getAlias());
		return buffer.toString();
	}

	
}
