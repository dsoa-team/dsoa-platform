package br.ufpe.cin.dsoa.api.service;

import java.util.List;

import br.ufpe.cin.dsoa.api.core.NamedElement;

public interface ComponentType extends NamedElement {
	String NAME = "name";
	String CLASSNAME = "classname";
	
	public String getClassname();
	public List<ProvidedPort> getProvidedPortList();
	public List<RequiredPort> getRequiredPortList();
	
}
