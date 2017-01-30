package br.ufpe.cin.dsoa.platform.service.selector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.RelationalOperator;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;

public class Normalizer {

	public static double[][] normalize(List<Constraint> constraints,
			List<ServiceInstance> candidates) {

		double matrix[][] = new double[candidates.size()][constraints.size()];

		for (int indexCandidate = 0; indexCandidate < candidates.size(); indexCandidate++) {
			ServiceInstance candidate = candidates.get(indexCandidate);
			List<Constraint> candidateConstraints = candidate.getPort().getServiceSpecification().getNonFunctionalSpecification().getConstraints();
			
			Map<String, Constraint> mapConstraint = toMap(candidateConstraints);

			for (int indexConstraint = 0; indexConstraint < constraints.size(); indexConstraint++) {
				Constraint requiredConstraint = constraints.get(indexConstraint);
				matrix[indexCandidate][indexConstraint] = mapConstraint.get(constraintKey(requiredConstraint)).getThreashold(); 
			}
		}
	
		double[][] normalized = new double[candidates.size()][constraints.size()];
		normalized = normalizeMatrix(matrix, min(matrix), max(matrix), constraints);
		
		return normalized;
	};
	
	public static double[][] normalizeMatrix(double[][] matrix, double[]min, double[]max, List<Constraint> constraints){
		
		double[][] normalized = new double[matrix.length][matrix[0].length];
		
		for(int y = 0; y < matrix[0].length; y++){
			RelationalOperator expression = constraints.get(y).getExpression(); 
			
			for(int x=0; x < matrix.length; x++){
				
				if(expression.equals(RelationalOperator.LT) || expression.equals(RelationalOperator.LE)) {
					normalized[x][y] = (min[y])/(matrix[x][y]);
				} else if(expression.equals(RelationalOperator.GT) || expression.equals(RelationalOperator.GE)) {
					normalized[x][y] = (matrix[x][y])/(max[y]);
				} else {
					normalized[x][y] = 0;
				}
			}
		}
		
		return normalized;
	}
	
	public static double[] min(double[][] matrix){
		double min[] = new double[matrix[0].length];
		
		for(int x=0; x < min.length; x++){
			double minValue = Double.MAX_VALUE;
			
			for(int y = 0; y < matrix.length; y++){
				if(matrix[y][x] < minValue){
					minValue = matrix[y][x];
					min[x] = minValue;
				}
			}
		}
		
		return min;
	}
	
	public static double[] max(double[][] matrix){
		double max[] = new double[matrix[0].length];
		
		for(int x=0; x < max.length; x++){
			double maxValue = Double.MIN_VALUE;
			
			for(int y = 0; y < matrix.length; y++){
				if(matrix[y][x] > maxValue){
					maxValue = matrix[y][x];
					max[x] = maxValue;
				}
			}
		}
		
		return max;
	}

	/**
	 * <qos.responsetime.getCotation, Constraint>
	 * <qos.availability, Constraint>
	 * 
	 * @param constraints
	 * @return
	 */
	public static Map<String, Constraint> toMap(List<Constraint> constraints) {

		Map<String, Constraint> map = new HashMap<String, Constraint>();

		for (Constraint constraint : constraints) {
			map.put(constraintKey(constraint), constraint);
		}
		return map;
	}

	public static String constraintKey(Constraint constraint) {
		String key = "";
		if (constraint.getOperation() != null && !constraint.getOperation().isEmpty()) {
			key = String.format("%s.%s", constraint.getAttributeId(), constraint.getOperation());
		} else {
			key = constraint.getAttributeId();
		}

		return key;
	}
}