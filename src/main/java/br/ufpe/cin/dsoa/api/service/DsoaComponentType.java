package br.ufpe.cin.dsoa.api.service;

import java.util.List;

public interface DsoaComponentType extends NamedElement {
	String NAME = "name";
	String CLASSNAME = "classname";
	
	public String getClassname();
	public List<ProvidedPort> getProvidedPortList();
	public List<RequiredPort> getRequiredPortList();
	
	public void addRequiredPort(RequiredPort requiredPort);
	public void addProvidedPort(ProvidedPort providedPort);
}
