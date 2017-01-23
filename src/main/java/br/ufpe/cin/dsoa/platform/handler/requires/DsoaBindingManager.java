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
public class DsoaBindingManager implements ConstraintViolationListener {

	/** 
	 * The meta-object representing a binding between the field and a ServiceInstance 
	 */
	private Binding binding;

	/**
	 * The list of undesirable services (represented by their ids)
	 */
	private List<String> blackList;	
	
	/**
	 * An internal element used to receive notifications concerning services arrivals and
	 * departures on the supporting platform
	 */
	private ServiceListener serviceListener;

	public DsoaBindingManager(Binding binding) {
		super();
		this.blackList = new ArrayList<String>();
		this.binding = binding;
		this.serviceListener = new ServiceListenerImpl();
	}
	
	public List<String> getBlackList() {
		return blackList;
	}

	/**
	 * When a dependency is starting, it must start corresponding manager (DependencyManager) which will configure
	 * its autonomic loop
	 */
	public void start() {
		this.resolve();
	}
	
	public void stop() {
		// Just to nothing since the binding is already unbound...
	}
	
	private void resolve() {
		((BindingImpl)binding).getDsoaPlatform().getServiceRegistry().getBestService(binding.getPort().getServiceSpecification(),
				getBlackList(), this.serviceListener);
	}

	@Override
	public void constraintViolated(String serviceId, Constraint constraint,
			AttributeValue value) {
		synchronized (binding) {
			this.evaluate(serviceId, constraint, value);
		}
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
		this.resolve();
	}
	
	private class ServiceListenerImpl implements ServiceListener {
		
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
		}
		
		public void onError(Exception e) {
		}

		public String getServiceInterfaceName() {
			return binding.getPort().getServiceSpecification().getFunctionalInterface().getInterfaceName();
		}
	}
	
}
