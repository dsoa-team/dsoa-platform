package br.ufpe.cin.dsoa.handler.dependency;

import br.ufpe.cin.dsoa.contract.Service;
import br.ufpe.cin.dsoa.contract.ServiceConsumer;

public class ServiceDependency {

	private ServiceConsumer consumer;
	private DependencyMetadata metadata;
	private Service service;
	private boolean valid;
	

	public ServiceDependency(ServiceConsumer consumer, DependencyMetadata sla) {
		this.metadata = sla;
		this.consumer = consumer;
		this.valid = false;
	}

	public Object onGet(Object arg0, String arg1, Object arg2) {
		return service.getServiceObject();
	}

	public void onSet(Object arg0, String arg1, Object arg2) {
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
	}
}
