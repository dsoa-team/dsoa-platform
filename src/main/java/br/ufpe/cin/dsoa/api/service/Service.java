package br.ufpe.cin.dsoa.api.service;

import java.util.Dictionary;


public interface Service {

	String getCompomentId();

	ServiceSpecification getSpecification();
	
	public Dictionary<?, ?> getProperties();

	Object getServiceObject();

	void ungetServiceObject();

}