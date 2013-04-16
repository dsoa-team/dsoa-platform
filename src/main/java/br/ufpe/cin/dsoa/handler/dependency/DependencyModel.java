package br.ufpe.cin.dsoa.handler.dependency;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.contract.Service;
import br.ufpe.cin.dsoa.contract.ServiceConsumer;

public class DependencyModel {
	private Dependency manager;
	private ServiceConsumer consumer;
	private DependencyMetadata metadata;
	private Service service;
	private boolean valid;
	

	public DependencyModel(DependencyHandler handler, ServiceConsumer consumer, DependencyMetadata sla) {
		//this.handler = handler;
		this.metadata = sla;
		this.consumer = consumer;
		this.valid = false;
	}

	public void start() {
		//this.manager.resolve();
	}

	public void setDependencyManager(Dependency manager) {
		this.manager = manager;
	}

	public Object onGet(Object arg0, String arg1, Object arg2) {
		return service.getServiceObject();
	}

	public void onSet(Object arg0, String arg1, Object arg2) {
	}

	public BundleContext getContext() {
		return null;//handler.getInstanceManager().getContext();
	}

	public DependencyMetadata getMetadata() {
		return metadata;
	}

	public ServiceConsumer getConsumer() {
		return consumer;
	}

	public boolean isValid() {
		return valid;
	}
	
	public void setService(Service service) {
		this.service = service;
		this.setValid(true);
	}
	
	private void setValid(boolean valid) {
		this.valid = valid;
		//this.handler.checkValidate();
	}
}
