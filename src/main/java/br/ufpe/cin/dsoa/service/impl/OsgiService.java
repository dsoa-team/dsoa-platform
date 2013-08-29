package br.ufpe.cin.dsoa.service.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.service.AttributeConstraint;
import br.ufpe.cin.dsoa.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.service.Service;
import br.ufpe.cin.dsoa.service.ServiceSpecification;
import br.ufpe.cin.dsoa.util.AttributeParser;
import br.ufpe.cin.dsoa.util.Util;

public class OsgiService implements Service {

	private String serviceId;
	private Object serviceObject;
	private ServiceReference reference;
	private ServiceSpecification spec;

	public OsgiService(ServiceReference reference)
			throws ClassNotFoundException {
		super();
		this.serviceId = Util.getId(reference);

		String keys[] = reference.getPropertyKeys();
		List<AttributeConstraint> attConstraints = new ArrayList<AttributeConstraint>();
		for (String key : keys) {
			Object value = reference.getProperty(key);
			AttributeConstraint attConstraint = AttributeParser.parse(key,
					value);
			if (attConstraint != null) {
				attConstraints.add(attConstraint);
			}
		}

		NonFunctionalSpecification nonFunctionalSpecification = null;
		String serviceInterface = null;
		Class<?> clazz = null;

		if (!attConstraints.isEmpty()) {
			nonFunctionalSpecification = new NonFunctionalSpecification(
					attConstraints);
		}
		serviceInterface = (String) reference
				.getProperty(Constants.OBJECTCLASS);
		clazz = reference.getBundle().loadClass(serviceInterface);

		this.spec = new ServiceSpecification(clazz, serviceInterface,
				nonFunctionalSpecification);
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
