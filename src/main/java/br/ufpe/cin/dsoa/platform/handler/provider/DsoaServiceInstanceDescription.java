package br.ufpe.cin.dsoa.platform.handler.provider;

import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.api.service.impl.ServiceInstanceImpl;
import br.ufpe.cin.dsoa.util.Constants;

public class DsoaServiceInstanceDescription {
	
	private ServiceInstance serviceInstance;

	public DsoaServiceInstanceDescription(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}

	public boolean isValid() {
		return ((ServiceInstanceImpl)serviceInstance).isPublished();
	}
	
	public Element getInfo() {
		Element elDependency = new Element(Constants.PROVIDES_TAG, Constants.PROVIDES_TAG_NAMESPACE);
		elDependency.addAttribute(new Attribute("service-instance", serviceInstance.getName()));
		elDependency.addAttribute(new Attribute("interface", serviceInstance.getPort().getServiceSpecification().getFunctionalInterface().getInterfaceName()));
		elDependency.addAttribute(new Attribute("published", String.valueOf(isValid())));
		return elDependency;
	}
}
