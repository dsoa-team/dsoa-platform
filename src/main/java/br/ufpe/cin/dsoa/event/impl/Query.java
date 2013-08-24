package br.ufpe.cin.dsoa.event.impl;

public class Query {

	private String id;
	private String queryString;

	public Query(String id, String queryString) {
		super();
		this.id = id;
		this.queryString = queryString;
	}

	public String getId() {
		return id;
	}

	public String getQueryString() {
		return queryString;
	}

}
