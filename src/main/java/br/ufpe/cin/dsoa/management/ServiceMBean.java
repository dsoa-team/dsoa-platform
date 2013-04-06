package br.ufpe.cin.dsoa.management;

import org.osgi.framework.ServiceReference;

public class ServiceMBean {

	private ServiceReference reference;

	public ServiceMBean(ServiceReference reference) {
		this.reference = reference;
	}

}
