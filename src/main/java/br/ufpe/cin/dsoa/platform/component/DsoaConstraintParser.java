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
