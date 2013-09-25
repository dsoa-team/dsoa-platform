package br.ufpe.cin.dsoa.api.service;

public enum Expression {
	GT("GT", ">",  "%s>%s",    "LE"), 
	LT("LT", "<",  "%s<%s",    "GE"), 
	EQ("EQ", "=",  "%s=%s",    "NE"), 
	GE("GE", ">=", "%s>=%s",   "LT"), 
	LE("LE", "<=", "%s<=%s",   "GT"), 
	NE("NE", "!=", "!(%s=%s)", "EQ");

	private String alias;
	private String operator;
	private String complementAlias;
	private String format;

	Expression(String alias, String operator, String format, String complementAlias) {
		this.operator = operator;
		this.alias = alias;
		this.format = format;
		this.complementAlias = complementAlias;
	}

	public String getOperator() {
		return operator;
	}

	public String getAlias() {
		return alias;
	}
	
	public String renderExpression(String op1, String op2) {
		String expressionString = String.format(this.format, op1, op2);
		
		return expressionString;
	}

	public Expression getComplement() {
		Expression complement = Expression.valueOf(this.complementAlias);
		return complement;
	}

	public String toString() {
		return this.operator;
	}
}
