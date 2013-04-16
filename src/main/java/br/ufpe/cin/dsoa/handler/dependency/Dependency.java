package br.ufpe.cin.dsoa.handler.dependency;

import org.apache.felix.ipojo.FieldInterceptor;

public class Dependency implements FieldInterceptor {
	
	private Class<?> specification;
	
	public void start() {
		
	}
	
	public boolean isValid() {
		return false;
	}
	
	public Class<?> getSpecification() {
		return this.specification;
	}
	
	@Override
	public void onSet(Object pojo, String fieldName, Object value) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Object onGet(Object pojo, String fieldName, Object value) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
