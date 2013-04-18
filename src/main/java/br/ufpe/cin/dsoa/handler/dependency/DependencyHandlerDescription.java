package br.ufpe.cin.dsoa.handler.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

public class DependencyHandlerDescription extends HandlerDescription {

	private static final String STATUS_ELEMENT = "status";
	private List<DependencyDescription> depDescriptions;

	public DependencyHandlerDescription(DependencyHandler depHandler, List<Dependency> deps) {
		super(depHandler);
		depDescriptions = new ArrayList<DependencyDescription>(deps.size());
        for (Dependency dependency : deps)  {
            depDescriptions.add(new DependencyDescription(dependency));
        }
	}
	
	public Element getHandlerInfo() {
        Element deps = super.getHandlerInfo();
        String state = "valid";
        for (DependencyDescription depDescription : depDescriptions) {
            if (!depDescription.isValid()) {
                state = "invalid";
            }
            Element dep = depDescription.getInfo();
            deps.addElement(dep);
        }
        deps.addAttribute(new Attribute(STATUS_ELEMENT, state));
        return deps;
    }

}
