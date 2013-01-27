package br.ufpe.cin.dsoa.event;

import java.util.HashMap;


public class Context extends HashMap<String, Object>{
	private String id;

	public Context(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	
}
