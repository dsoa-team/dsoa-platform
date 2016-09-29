package br.ufpe.cin.dsoa.platform.registry;

import java.util.List;

import org.osgi.framework.InvalidSyntaxException;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;

public class InvalidServiceSpecificationException extends RuntimeException {

	private static final long serialVersionUID = 6448160017317723239L;
	
	private String serviceInterface;
	private List<AttributeConstraint> constraints;
	
	public InvalidServiceSpecificationException(String message, String serviceInterface, List<AttributeConstraint> constraints) {
		super(message);
		this.serviceInterface = serviceInterface;
		this.constraints = constraints;
	}
	
	public InvalidServiceSpecificationException(String message, ServiceSpecification spec, InvalidSyntaxException e) {
		super(message, e);
		this.serviceInterface = spec.getServiceInterface();
		this.constraints = (spec.getNonFunctionalSpecification() == null ? null :spec.getNonFunctionalSpecification().getAttributeConstraints());
	}

	public InvalidServiceSpecificationException(String message,
			String serviceInterface) {
		this(message, serviceInterface, null);
	}

	public String getServiceInterface() {
		return serviceInterface;
	}
	
	public List<AttributeConstraint> getConstraints() {
		return constraints;
	}
}
