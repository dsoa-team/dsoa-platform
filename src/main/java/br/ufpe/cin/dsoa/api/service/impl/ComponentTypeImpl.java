package br.ufpe.cin.dsoa.api.service.impl;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.dsoa.api.core.impl.NamedElementImpl;
import br.ufpe.cin.dsoa.api.service.ComponentType;
import br.ufpe.cin.dsoa.api.service.ProvidedPort;
import br.ufpe.cin.dsoa.api.service.RequiredPort;

public class ComponentTypeImpl extends NamedElementImpl implements ComponentType {

	private String classname;
	private List<ProvidedPort> providedPortList;
	private List<RequiredPort> requiredPortList;
	
	public ComponentTypeImpl(String name, String classname) {
		super(name);
		this.classname = classname;
		this.providedPortList = new ArrayList<ProvidedPort>();
		this.requiredPortList = new ArrayList<RequiredPort>();
	}

	
	@Override
	public List<ProvidedPort> getProvidedPortList() {
		return providedPortList;
	}

	@Override
	public List<RequiredPort> getRequiredPortList() {
		return requiredPortList;
	}

	public void addRequiredPort(RequiredPort requiredPort) {
		this.requiredPortList.add(requiredPort);
	}
	
	public void addProvidedPort(ProvidedPort providedPort) {
		this.providedPortList.add(providedPort);
	}

	@Override
	public String getClassname() {
		return classname;
	}

}
