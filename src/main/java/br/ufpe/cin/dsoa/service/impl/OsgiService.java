package br.ufpe.cin.dsoa.service.impl;

import java.lang.reflect.Proxy;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.platform.monitor.ServiceProxy;
import br.ufpe.cin.dsoa.service.Service;
import br.ufpe.cin.dsoa.service.ServiceSpecification;
import br.ufpe.cin.dsoa.util.Util;

public class OsgiService implements Service {

	private String serviceId;
	private Object serviceObject;
	private ServiceReference reference;
	private OsgiServiceSpecification spec;

	public OsgiService(ServiceReference reference) {
		super();
		this.serviceId = Util.getId(reference);
		this.spec = new OsgiServiceSpecification(reference);
		this.reference = reference;
	}
	
	public String getServiceId() {
		return serviceId;
	}

	public Object getServiceObject() {
		if (serviceObject == null) {
			serviceObject = reference.getBundle().getBundleContext().getService(reference);
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

	public Object getProxy() {
		return Proxy.newProxyInstance(this.spec.getClassloader(), this.spec.getClasses(),
				new ServiceProxy(this));
	}
	
	public Dictionary getProperties() {
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
