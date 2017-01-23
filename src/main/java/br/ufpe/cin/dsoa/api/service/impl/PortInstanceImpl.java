package br.ufpe.cin.dsoa.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.service.Port;
import br.ufpe.cin.dsoa.api.service.PortInstance;
import br.ufpe.cin.dsoa.api.service.Property;

public class PortInstanceImpl extends NamedElementImpl implements PortInstance {
	
	private Port port;
	private Map<String,Property> propMap;
	
	public PortInstanceImpl(String name) {
		super(name);
		this.propMap = new HashMap<String,Property>();
	}
	
	public PortInstanceImpl(String name, Port port, List<Property> props) {
		this(name);
		this.port = port;
		for (Property property: props) {
			propMap.put(property.getName(),	property);
		}
	}
	
	@Override
	public void setPort(Port port) {
		this.port = port;
	}

	@Override
	public Port getPort() {
		return this.port;
	}

	@Override
	public List<String> getPropertyNames() {
		return new ArrayList<String>(propMap.keySet());
	}

	@Override
	public Object getProperty(String name) {
		return propMap.get(name).getValue();
	}

	@Override
	public void addProperty(String name, Object value, String type) {
		this.propMap.put(name,new PropertyImpl(name, value, type));
	}

	@Override
	public String getPropertyType(String name) {
		return this.propMap.get(name).getType();
	}

}
