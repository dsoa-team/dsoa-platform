package br.ufpe.cin.dsoa.handler.dependency;

import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.util.Constants;

public class DependencyDescription {

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
		Element elDependency = new Element(Constants.REQUIRES_TAG, Constants.REQUIRES_TAG_NAMESPACE);
		elDependency.addAttribute(new Attribute(DEPENDENCY_ATT_VALID, String.valueOf(dependency.isValid())));
		elDependency.addAttribute(new Attribute(DEPENDENCY_ATT_SPECIFICATION, dependency.getSpecification().getName()));
		return elDependency;
	}
}
