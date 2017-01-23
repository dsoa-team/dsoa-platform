package br.ufpe.cin.dsoa.api.service;

import java.util.List;

public interface PortInstance extends NamedElement {

	public void setPort(Port port) ;

	public Port getPort();
	
	public List<String> getPropertyNames();
	
	public Object getProperty(String name);
	
	public String getPropertyType(String name);
	
	public void addProperty(String name, Object value, String type);
}
