package br.ufpe.cin.dsoa.platform.component;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.RelationalOperator;
import br.ufpe.cin.dsoa.api.service.impl.ConstraintImpl;
import br.ufpe.cin.dsoa.util.Constants;

public class DsoaConstraintParser {
	
	/**
	 * The configuration properties will only be passed by the DsoaProvidesHandler in order to add the non-functional
	 * requirements to the collection of properties that are to be included in the OSGi Service Registry. When this method
	 * is called from the DsoaRequiresHandler, it will pass a null configurationProperties object.
	 */
	@SuppressWarnings("rawtypes")
	public static List<Constraint> getConstraintList(Element[] constraintTags, Dictionary configurationProperties) {
		List<Constraint> constraintList = new ArrayList<Constraint>();
		String metric = null, operation = null, expression = null, threashold = null, weight = null;
		
		if(constraintTags != null){
			/* Executes an iteration for each constraint tag */
			for (Element constraintTag : constraintTags) {
				operation = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_OPERATION);
				metric = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_METRIC);
				expression = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_EXPRESSION);
				threashold = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_THREASHOLD);
				weight = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_WEIGHT);
				if (configurationProperties != null) {
					configurationProperties.put(getOsgiConstraintKey(metric, operation, expression), threashold);
				}
				constraintList.add(defineConstraint(metric, operation, expression, threashold, weight));
			}
		}
		return constraintList;
	}
	
	public static String getOsgiConstraintKey(String metric, String operation, String expression) {
		StringBuilder builder = new StringBuilder();
		String constraintType = (operation == null) ? Attribute.SERVICE_CONSTRAINT : Attribute.OPERATION_CONSTRAINT;
		builder.append(constraintType);
		builder.append(Constants.TOKEN);
		builder.append(metric);
		builder.append(Constants.TOKEN);
		builder.append((operation != null) ? operation : "");
		builder.append(Constants.TOKEN);
		builder.append(expression);
		return builder.toString();
	}

	
	/**
	 * This method is responsible for translating service properties into attribute constraints.
	 * @param reference
	 * @return
	 */
	public static List<Constraint> getAttributeConstraints(ServiceReference reference) {
		String keys[] = reference.getPropertyKeys();
		List<Constraint> attConstraints = new ArrayList<Constraint>();
		for (String key : keys) {
			if (key != null && key.toLowerCase().startsWith(Attribute.SERVICE_CONSTRAINT) || key.toLowerCase().startsWith(Attribute.OPERATION_CONSTRAINT) ) {
				Object value = reference.getProperty(key);
				Constraint attConstraint = parse(key, value);
				if (attConstraint != null) {
					attConstraints.add(attConstraint);
				}
			}
		}
		return attConstraints;
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
				attributeConstraint = new ConstraintImpl(attributeId, operationName, exp, doubleVal, ConstraintImpl.WEIGHT_UNSET);
		}
		return attributeConstraint;
	}	
	
	public static ConstraintImpl defineConstraint(String attribute, String operation, String expression, String threashold, String weight) {
		RelationalOperator exp = RelationalOperator.valueOf(expression);
		double thr = Double.parseDouble(threashold);
		double wgt = ConstraintImpl.WEIGHT_UNSET;
		if (null != weight) {
			wgt = Double.parseDouble(weight);
		}
		return new ConstraintImpl(attribute, operation, exp, thr, wgt);
	}
}
