package br.ufpe.cin.dsoa.handler.dependency;

import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

public class DependencyHandlerDescription extends HandlerDescription {

	private static final String STATUS_ELEMENT = "status";
	private DependencyDescription[] depDescriptions;

	public DependencyHandlerDescription(DependencyHandler depHandler, Dependency[] deps) {
		super(depHandler);
		depDescriptions = new DependencyDescription[deps.length];
        for (int i = 0; i < depDescriptions.length; i++) {
            depDescriptions[i] = new DependencyDescription(deps[i]);
        }
	}
	
	public Element getHandlerInfo() {
        Element deps = super.getHandlerInfo();
        String state = "valid";
        for (int i = 0; i < depDescriptions.length; i++) {
            if (!depDescriptions[i].isValid()) {
                state = "invalid";
            }
            Element dep = depDescriptions[i].getInfo();
            deps.addElement(dep);
        }
        deps.addAttribute(new Attribute(STATUS_ELEMENT, state));
        return deps;
    }

}
