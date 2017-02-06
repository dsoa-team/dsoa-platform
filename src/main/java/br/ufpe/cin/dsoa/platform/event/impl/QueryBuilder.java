package br.ufpe.cin.dsoa.platform.event.impl;

public interface QueryBuilder {
	
	public void buildContextClause();
	
	public void buildInsertIntoClause();

	public void buildSelectClause();

	public void buildFromClause();
	
	public void buildFilterClause();
	
	public void buildWindowClause();

	public void buildAliasClause();

	public void buildWhereClause();

	public void buildGroupByClause();

	public void buildHavingClause();

	public Query getQuery();

	public String getContext();

}
