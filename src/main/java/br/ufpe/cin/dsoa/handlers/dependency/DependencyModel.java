package br.ufpe.cin.dsoa.handlers.dependency;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.contract.ServiceConsumer;

public class Dependency {
	private DependencyManager manager;
	private ServiceConsumer consumer;
	private DependencyMetadata metadata;
	private ServiceModel serviceModel;
	private boolean valid;
	

	public ServiceDependency(DependencyHandler handler, ServiceConsumer consumer, DependencyMetadata sla) {
		this.handler = handler;
		this.metadata = sla;
		this.consumer = consumer;
		this.valid = false;
	}

	public void start() {
		this.manager.resolve();
	}

	public void setDependencyManager(DependencyManager manager) {
		this.manager = manager;
	}

	public Object onGet(Object arg0, String arg1, Object arg2) {
		return serviceModel.getService();
	}

	public void onSet(Object arg0, String arg1, Object arg2) {
	}

	public BundleContext getContext() {
		return handler.getInstanceManager().getContext();
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
	
	public void setService(ServiceModel serviceModel) {
		this.serviceModel = serviceModel;
		this.setValid(true);
	}
	
	private void setValid(boolean valid) {
		this.valid = valid;
		this.handler.checkValidate();
	}
}
