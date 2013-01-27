package br.ufpe.cin.dsoa.handlers.dependency;

import org.apache.felix.ipojo.FieldInterceptor;

public abstract class DependencyManager implements FieldInterceptor {
	
	public abstract void start();
	public abstract boolean isValid();
	
}
