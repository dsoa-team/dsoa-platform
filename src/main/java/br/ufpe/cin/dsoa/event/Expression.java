package br.ufpe.cin.dsoa.event;

public enum Expression {
	GT(">="), LT("<="), EQ("=");
	
	private String operator;
	
	Expression(String exp) {
		this.operator = exp;
	}
	
	public String getOperator() {
		return operator;
	}
	
}
