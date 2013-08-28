package br.ufpe.cin.dsoa.platform.event.impl;

public class Statement {
	
	private String name;
	
	private String query;

	public Statement(String name, String query) {
		super();
		this.name = name;
		this.query = query;
	}

	public String getName() {
		return name;
	}

	public String getQuery() {
		return query;
	}

}
