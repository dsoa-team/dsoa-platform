package br.ufpe.cin.dsoa.api.service;


public interface Constraint {

	public abstract String getAttributeId();

	public abstract String getOperation();

	public abstract RelationalOperator getExpression();

	public abstract double getThreashold();
	
	public double getWeight();

	public abstract String format();

}