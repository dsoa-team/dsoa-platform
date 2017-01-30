package br.ufpe.cin.dsoa.platform.handler.requires;

import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.api.service.Binding;
import br.ufpe.cin.dsoa.api.service.Constraint;

public class DsoaBindingDescription {

	private static final String DEPENDENCY_ATT_VALID = "valid";
	private static final String DEPENDENCY_ATT_SPECIFICATION = "interface";
	
	private Binding binding;

	public DsoaBindingDescription(Binding binding) {
		this.binding = binding;
	}

	public boolean isValid() {
		return binding.isValid();
	}
	
	public Element getInfo() {
		Element elDependency = new Element("Binding","");
		elDependency.addAttribute(new Attribute("binding-name", binding.getName()));
		elDependency.addAttribute(new Attribute(DEPENDENCY_ATT_SPECIFICATION, binding.getPort().getServiceSpecification().getFunctionalInterface().getInterfaceName()));
		elDependency.addAttribute(new Attribute(DEPENDENCY_ATT_VALID, String.valueOf(binding.isValid())));
		
		Element elNFEElem = new Element("Constraints","");
		elDependency.addElement(elNFEElem);
		for (Constraint c : this.binding.getPort().getServiceSpecification().getNonFunctionalSpecification().getConstraints()) {
			Element elC = new Element("Constraint",null);
			elC.addAttribute(new Attribute("Attribute", c.getAttributeId()));
			elC.addAttribute(new Attribute("Operation", c.getOperation()));
			elC.addAttribute(new Attribute("Operator", c.getExpression().getOperator()));
			elC.addAttribute(new Attribute("Threashold", String.valueOf(c.getThreashold())));
			elNFEElem.addElement(elC);
		}
		
		if (binding.isValid()) {
			elDependency.addAttribute(new Attribute("bound-to", binding.getServiceInstanceProxy().getName()));
		}
		return elDependency;
	}
}
