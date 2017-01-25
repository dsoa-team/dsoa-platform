package br.ufpe.cin.dsoa.platform.handler.requires;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.service.Binding;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.api.service.impl.BindingImpl;
import br.ufpe.cin.dsoa.platform.attribute.ConstraintViolationListener;

/**
 * This class implements an autonomic binding manager, which is responsible for managing a Binding meta-object.
 * In terms of taxonomy, the BindingManager represents a Reflective Computational Element, while the Binding
 * represents a Reflective Meta-data Element (vide Forms) 
 * 
 * @author fabions
 */
public class DsoaBindingManager implements ConstraintViolationListener, ServiceListener {

	/** 
	 * The meta-object representing a binding between the field and a ServiceInstance 
	 */
	private Binding binding;

	/**
	 * The list of undesirable services (represented by their ids)
	 */
	private List<String> blackList;	
	

	public DsoaBindingManager(Binding binding) {
		super();
		this.blackList = new ArrayList<String>();
		this.binding = binding;
	}
	
	public List<String> getBlackList() {
		return blackList;
	}

	public void serviceSelection() {
		((BindingImpl)binding).getDsoaPlatform().getServiceRegistry().getBestService(binding.getPort().getServiceSpecification(),
				getBlackList(), this);
	}

	@Override
	public void constraintViolated(String serviceId, Constraint constraint,
			AttributeValue value) {
			this.evaluate(serviceId, constraint, value);
	}
	
	public void evaluate(String serviceId, Constraint constraint, AttributeValue value) {
		System.err.println("====================================================");
		System.err.println("ServiceId: " + serviceId);
		String op1 = constraint.getAttributeId();
		if (constraint.getOperation() != null) {
			op1 = "." + constraint.getOperation();
		}
		String expected = constraint.getExpression().renderExpression(op1,
				constraint.getThreashold() + "");
		System.err.println(String.format("Expected value= %s :: Monitored Value= %s", expected,
				value.getValue()));

		System.err.println("====================================================");
		
		this.binding.unbind();
		this.serviceSelection();
	}
	
	/**
	 * This method is called whenever a compatible service is chosen to be used by this component.
	 * 
	 */
	public void onArrival(ServiceInstance service) {
		binding.bind(service);
	}

	public void onDeparture(ServiceInstance service) {
		binding.unbind();
		blackList.clear();//TODO:REMOVE
		serviceSelection();
	}
	
	public void onError(Exception e) {
	}

	public String getServiceInterfaceName() {
		return binding.getPort().getServiceSpecification().getFunctionalInterface().getInterfaceName();
	}
	
}
