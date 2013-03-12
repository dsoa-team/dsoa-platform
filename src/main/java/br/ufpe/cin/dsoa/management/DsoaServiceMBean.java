package br.ufpe.cin.dsoa.management;

import org.osgi.framework.ServiceReference;

public class DsoaServiceMBean {

	private ServiceReference reference;

	public DsoaServiceMBean(ServiceReference reference) {
		this.reference = reference;
	}

}
