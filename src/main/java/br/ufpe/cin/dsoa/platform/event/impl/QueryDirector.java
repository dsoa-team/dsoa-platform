package br.ufpe.cin.dsoa.platform.event.impl;

public class QueryDirector {

	QueryBuilder builder;
	
	public QueryDirector(QueryBuilder builder) {
		this.builder = builder;
	}
	
	public void construct(){
		this.builder.buildContextClause();
		this.builder.buildInsertIntoClause();
		this.builder.buildSelectClause();
		this.builder.buildFromClause();
		this.builder.buildFilterClause();
		this.builder.buildWindowClause();
		this.builder.buildAliasClause();
		this.builder.buildWhereClause();
		this.builder.buildGroupByClause();
		this.builder.buildHavingClause();
	}
	
	public Query getQuery(){
		return this.builder.getQuery();
	}
	
	public String getContext() {
		return builder.getContext();
	}
}
