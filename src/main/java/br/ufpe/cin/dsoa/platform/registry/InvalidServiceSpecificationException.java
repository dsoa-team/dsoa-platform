package br.ufpe.cin.dsoa.platform.registry;

import java.util.List;

import org.osgi.framework.InvalidSyntaxException;

import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.impl.ServiceSpecification;

public class InvalidServiceSpecificationException extends RuntimeException {

	private static final long serialVersionUID = 6448160017317723239L;
	
	private String serviceInterface;
	private List<Constraint> constraints;
	
	public InvalidServiceSpecificationException(String message, String serviceInterface, List<Constraint> constraints) {
		super(message);
		this.serviceInterface = serviceInterface;
		this.constraints = constraints;
	}
	
	public InvalidServiceSpecificationException(String message, ServiceSpecification spec, InvalidSyntaxException e) {
		super(message, e);
		this.serviceInterface = spec.getFunctionalInterface().getInterfaceName();
		this.constraints = (spec.getNonFunctionalSpecification() == null ? null :spec.getNonFunctionalSpecification().getConstraints());
	}

	public InvalidServiceSpecificationException(String message,
			String serviceInterface) {
		this(message, serviceInterface, null);
	}

	public String getServiceInterface() {
		return serviceInterface;
	}
	
	public List<Constraint> getConstraints() {
		return constraints;
	}
}
