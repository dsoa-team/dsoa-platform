package br.ufpe.cin.dsoa.platform.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.api.service.RelationalOperator;
import br.ufpe.cin.dsoa.api.service.impl.ConstraintImpl;
import br.ufpe.cin.dsoa.util.Constants;

public class DsoaConstraintParser {
	
	//private static Logger log = DsoaSimpleLogger.getDsoaLogger(DependencyHandler.class.getName(), true, false);
	
	public static List<ConstraintImpl> getConstraintList(Element[] constraintTags) {
		List<ConstraintImpl> constraintList = new ArrayList<ConstraintImpl>();
		String attribute = null, operation = null, expression = null, threashold = null;
		
		if(constraintTags != null){
			/* Executes an iteration for each constraint tag */
			for (Element constraintTag : constraintTags) {
				attribute = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_METRIC);
				operation = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_OPERATION);
				expression = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_EXPRESSION);
				threashold = constraintTag.getAttribute(Constants.CONSTRAINT_ATT_THREASHOLD);
				constraintList.add(DsoaConstraintParser.defineConstraint(attribute, operation, expression, threashold));
			}
		}
		return constraintList;
	}

	public static ConstraintImpl defineConstraint(String attribute, String operation, String expression, String threashold) {
		RelationalOperator exp = RelationalOperator.valueOf(expression);
		double thr = Double.parseDouble(threashold);
		return new ConstraintImpl(attribute, operation, exp, thr);
	}
}
