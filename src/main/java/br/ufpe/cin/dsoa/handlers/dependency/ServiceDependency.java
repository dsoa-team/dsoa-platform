package br.ufpe.cin.dsoa.handlers.dependency;

import org.apache.felix.ipojo.FieldInterceptor;
import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.AdaptationManager;
import br.ufpe.cin.dsoa.contract.Sla;

public class ServiceDependency implements FieldInterceptor {

	/* Handler responsável por tratar as dependências da aplicação */
	private DependencyHandler handler;
	
	/* Status */
	private boolean valid;
	
	/* SLA */
	private Sla sla;
	
	/**
	 * Refers to the service that is under use. This reference can be dynamically changed when the underline 
	 * service can not provide the desired QoS level.
	 */
	private Object service;
	

	public ServiceDependency(DependencyHandler handler, Sla sla) {
		this.handler = handler;
		this.sla = sla;
	}

	public void start() {
		AdaptationManager.createManager(this);
	}

	public Object onGet(Object arg0, String arg1, Object arg2) {
		return service;
	}

	public void onSet(Object arg0, String arg1, Object arg2) {
		
	}

	public BundleContext getContext() {
		return handler.getInstanceManager().getContext();
	}

	public Sla getSla() {
		return sla;
	}

	public void setService(Object service) {
		this.service = service;
		this.setValid(true);
	}

	public boolean isValid() {
		return valid;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
		this.handler.checkValidate();
	}

}
