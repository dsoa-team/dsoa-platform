package br.ufpe.cin.dsoa.api.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.util.Constants;


public class AttributeConstraint {
	private String attributeId;
	private String operation;
	private Expression expression;
	private double threashold;
	private long weight;

	public AttributeConstraint(String attributeId, String operation, Expression expression, double threashold, long wgt) {
		this(attributeId, operation, expression,threashold);	
		this.weight = wgt;
	}
	
	public AttributeConstraint(String attributeId, String operation, Expression expression, double threashold) {
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
	public static AttributeConstraint parse(String key, Object value) {
		AttributeConstraint attributeConstraint = null;
		if (key != null && key.toLowerCase().startsWith(Attribute.SERVICE_CONSTRAINT) || key.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT) ) {
				Double doubleVal = null;
				if (NumberUtils.isNumber(value.toString())) {
					doubleVal = NumberUtils.createDouble(value.toString());
				}
				
				int index = key.lastIndexOf('.');
				String expStr = key.substring(index+1);
				Expression exp = Expression.valueOf(expStr);
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
				attributeConstraint = new AttributeConstraint(attributeId, operationName, exp, doubleVal);
		}
		return attributeConstraint;
	}
	
	public static List<AttributeConstraint> getAttributeConstraints(ServiceReference reference) {
		String keys[] = reference.getPropertyKeys();
		List<AttributeConstraint> attConstraints = new ArrayList<AttributeConstraint>();
		for (String key : keys) {
			Object value = reference.getProperty(key);
			if (key != null && key.toLowerCase().startsWith(Attribute.SERVICE_CONSTRAINT) || key.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT) ) {
				AttributeConstraint attConstraint = AttributeConstraint.parse(key,
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
