package br.ufpe.cin.dsoa.api.service.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.util.Util;

public class OsgiService implements Service {

	private String serviceId;
	private Object serviceObject;
	private ServiceReference reference;
	private ServiceSpecification spec;

	public static List<OsgiService> getOsgiServices(ServiceReference reference) throws ClassNotFoundException {
		List<OsgiService> svcList = new ArrayList<OsgiService>();
		String[] serviceInterfaces = (String[]) reference.getProperty(org.osgi.framework.Constants.OBJECTCLASS);
		for (String serviceInterface : serviceInterfaces) {
			svcList.add(getOsgiService(serviceInterface, reference));
		}
		return svcList;
	}
		
	public static OsgiService getOsgiService(String spec, ServiceReference reference) throws ClassNotFoundException {
		List<AttributeConstraint> attConstraints = AttributeConstraint.getAttributeConstraints(reference);
		NonFunctionalSpecification nonFunctionalSpecification = null;
		if (!attConstraints.isEmpty()) {
			nonFunctionalSpecification = new NonFunctionalSpecification(
					attConstraints);
		}
		Class<?> clazz = reference.getBundle().loadClass(spec);
		ServiceSpecification serviceSpec = new ServiceSpecification(clazz, spec, nonFunctionalSpecification);
		String serviceId = Util.getId(reference);
		return new OsgiService(serviceId, serviceSpec, reference);
	}
	
	private OsgiService(String id, ServiceSpecification spec, ServiceReference reference)
			throws ClassNotFoundException {
		super();
		this.serviceId = id;
		this.spec = spec;
		this.reference = reference;
	}

	public String getServiceId() {
		return serviceId;
	}

	public Object getServiceObject() {
		if (serviceObject == null) {
			serviceObject = reference.getBundle().getBundleContext()
					.getService(reference);
		}
		return serviceObject;
	}

	public void ungetServiceObject() {
		if (serviceObject == null) {
			reference.getBundle().getBundleContext().ungetService(reference);
		}
	}

	public ServiceReference getReference() {
		return reference;
	}

	public ServiceSpecification getSpecification() {
		return spec;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Dictionary<?,?> getProperties() {
		String[] keys = reference.getPropertyKeys();
		Dictionary dict = new Hashtable();
		for (String key : keys) {
			if (!key.equals("service.managed")) {
				dict.put(key, reference.getProperty(key));
			}
		}
		return dict;
	}

}
