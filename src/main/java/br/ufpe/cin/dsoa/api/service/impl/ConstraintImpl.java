package br.ufpe.cin.dsoa.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.RelationalOperator;
import br.ufpe.cin.dsoa.util.Constants;


public class ConstraintImpl implements Constraint {
	
	/** Metric id */
	private String attributeId;
	
	/** Relational operator */
	private RelationalOperator expression;
	
	//INCLUDE UNIT
	
	/** value */
	private double threashold;
	
	/** Operation name*/
	private String operation;
	
	public ConstraintImpl(String attributeId, String operation, RelationalOperator expression, double threashold) {
		this(attributeId,expression,threashold);	
		this.operation = operation;
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
	
	/**
	 * This method assumes that attribute constraints are represented as properties following the format presented bellow:
	 * 
	 * 1. For constraints on service related attributes:
	 * 		<"constraint.service"> <"."> <att-id> <"."> <expression>
	 * 		eg. constraint.service.qos.availiability.GT = 95
	 * 
	 * 2. For constraints on operation related attributes:
	 *		<"constraint.operation"> <"."> <att-id> <"."> <operation> <"."> <expression>
	 *		eg. constraint.operation.qos.avgResponseTime.LT = 10
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static ConstraintImpl parse(String key, Object value) {
		ConstraintImpl attributeConstraint = null;
		if (key != null && key.toLowerCase().startsWith(Attribute.SERVICE_CONSTRAINT) || key.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT) ) {
				Double doubleVal = null;
				if (NumberUtils.isNumber(value.toString())) {
					doubleVal = NumberUtils.createDouble(value.toString());
				}
				
				int index = key.lastIndexOf('.');
				String expStr = key.substring(index+1);
				RelationalOperator exp = RelationalOperator.valueOf(expStr);
				String attributeId = key.substring(0, index);
				String operationName = null;
				if (attributeId.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT)) {
					attributeId = attributeId.replaceFirst(Attribute.OPERATION_CONSTRAINT + Constants.TOKEN, "");
					index = attributeId.lastIndexOf('.');
					operationName = attributeId.substring(index+1);
					attributeId = attributeId.substring(0, index);
				} else {
					attributeId = attributeId.replaceFirst(Attribute.SERVICE_CONSTRAINT + Constants.TOKEN, "");
				}
				attributeConstraint = new ConstraintImpl(attributeId, operationName, exp, doubleVal);
		}
		return attributeConstraint;
	}
	
	/**
	 * This method is responsible for translating service properties into attribute constraints.
	 * @param reference
	 * @return
	 */
	public static List<ConstraintImpl> getAttributeConstraints(ServiceReference reference) {
		String keys[] = reference.getPropertyKeys();
		List<ConstraintImpl> attConstraints = new ArrayList<ConstraintImpl>();
		for (String key : keys) {
			if (key != null && key.toLowerCase().startsWith(Attribute.SERVICE_CONSTRAINT) || key.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT) ) {
				Object value = reference.getProperty(key);
				ConstraintImpl attConstraint = ConstraintImpl.parse(key,
					value);
				if (attConstraint != null) {
					attConstraints.add(attConstraint);
				}
			}
		}
		return attConstraints;
	}
	
	/*public static void main(String args[] ) {
		AttributeConstraint opConstraint = parse ("constraint.operation.qos.performance.avgResponseTime.getCotation.LT",500);
		AttributeConstraint svConstraint = parse ("constraint.service.qos.availability.LT", 99);
		System.out.println(opConstraint);
		System.out.println(svConstraint);
	}*/

}
