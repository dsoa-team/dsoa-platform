package br.ufpe.cin.dsoa.handlers.dependency;

import org.apache.felix.ipojo.FieldInterceptor;
import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.AdaptationManager;
import br.ufpe.cin.dsoa.contract.ServiceImpl;
import br.ufpe.cin.dsoa.contract.ServiceConsumer;
import br.ufpe.cin.dsoa.contract.AggreementOffer;

public class ServiceDependency implements FieldInterceptor {

	/* Handler responsável por tratar as dependências da aplicação */
	private DependencyHandler handler;
	
	/* Consumer */
	private ServiceConsumer consumer;
	
	/* SLA */
	private AggreementOffer slaTemplate;

	/* Status */
	private boolean valid;
	
	/**
	 * Refers to the service that is under use. This reference can be dynamically changed when the underline 
	 * service can not provide the desired QoS level.
	 */
	private ServiceImpl service;
	

	public ServiceDependency(DependencyHandler handler, ServiceConsumer consumer, AggreementOffer sla) {
		this.handler = handler;
		this.slaTemplate = sla;
		this.consumer = consumer;
		this.valid = false;
	}

	public void start() {
		AdaptationManager.createManager(this);
	}

	public Object onGet(Object arg0, String arg1, Object arg2) {
		return service.getServiceObject();
	}

	public void onSet(Object arg0, String arg1, Object arg2) {
		
	}

	public BundleContext getContext() {
		return handler.getInstanceManager().getContext();
	}

	public AggreementOffer getSlaTemplate() {
		return slaTemplate;
	}

	public ServiceConsumer getConsumer() {
		return this.consumer;
	}

	public boolean isValid() {
		return valid;
	}
	
	public void setService(ServiceImpl service) {
		this.service = service;
		this.setValid(true);
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
		this.handler.checkValidate();
	}

}
