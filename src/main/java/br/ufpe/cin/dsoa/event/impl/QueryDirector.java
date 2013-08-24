package br.ufpe.cin.dsoa.event.impl;

public class QueryDirector {

	QueryBuilder builder;
	
	public QueryDirector(QueryBuilder builder) {
		this.builder = builder;
	}
	
	public void construct(){
		this.builder.buildSelectClause();
		this.builder.buildFromClause();
		this.builder.buildWhereClause();
		this.builder.buildGroupByClause();
		this.builder.buildHavingClause();
	}
	
	public Query getQuery(){
		return this.builder.getQuery();
	}
}
