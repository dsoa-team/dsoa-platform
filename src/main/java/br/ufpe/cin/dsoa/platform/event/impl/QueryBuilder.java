package br.ufpe.cin.dsoa.platform.event.impl;

public interface QueryBuilder {

	public void buildSelectClause();

	public void buildFromClause();

	public void buildWhereClause();

	public void buildGroupByClause();

	public void buildHavingClause();

	public Query getQuery();
}