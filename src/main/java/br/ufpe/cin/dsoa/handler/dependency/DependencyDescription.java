package br.ufpe.cin.dsoa.handler.dependency;

import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

public class DependencyDescription {

	public static final String DEPENDENCY_NAME = "Dependency";
	public static final String DEPENDENCY_NAMESPACE = "Dsoa";
	private static final String DEPENDENCY_ATT_VALID = "valid";
	private static final String DEPENDENCY_ATT_SPECIFICATION = "specification";
	
	private Dependency dependency;

	public DependencyDescription(Dependency dependency) {
		this.dependency = dependency;
	}

	public boolean isValid() {
		return dependency.isValid();
	}
	
	public Element getInfo() {
		Element elDependency = new Element(DEPENDENCY_NAME, DEPENDENCY_NAMESPACE);
		elDependency.addAttribute(new Attribute(DEPENDENCY_ATT_VALID, String.valueOf(dependency.isValid())));
		elDependency.addAttribute(new Attribute(DEPENDENCY_ATT_SPECIFICATION, dependency.getSpecification().getName()));
		return elDependency;
	}
}
