package br.ufpe.cin.dsoa.platform.resource.impl;

import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;

public class ServiceEvaluator implements Service {
	
	private Service service;
	private boolean started;
	private long sampleInterval;
	private List<Method> serviceMethods;

	public ServiceEvaluator(Service service) {
		this.service = service;
		this.started = false;
		this.sampleInterval = 2000;
	}
	

	public void start() {
		NonFunctionalSpecification nfs = this.service.getSpecification().getNonFunctionalSpecification();
		if (nfs != null) {
			List<AttributeConstraint> constraints = nfs.getAttributeConstraints();
			if (constraints != null && constraints.size() != 0) {
				Set<String> methods = new HashSet<String>();
				for (AttributeConstraint constraint: constraints) {
					String methodName = constraint.getOperation() ;
					if (methodName != null) {
						methods.add(constraint.getOperation());
					}
				}
				if (methods.size() > 0) {
					Class<?> serviceInterface = this.service.getSpecification().getClazz();
					Iterator<String> itr = methods.iterator();
					String methodName = null;
					while (itr.hasNext()) {
						methodName = itr.next();
						try {
							Method method = serviceInterface.getMethod(methodName, (Class<?>)null);
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					started = true;
				}
			} 
		}
	}

	public void run() {
		while(started) {
			
			try {
				Thread.sleep(this.sampleInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public String getProviderId() {
		return this.service.getProviderId();
	}

	@Override
	public ServiceSpecification getSpecification() {
		return this.service.getSpecification();
	}

	@Override
	public Dictionary<?, ?> getProperties() {
		return this.service.getProperties();
	}

	@Override
	public Object getServiceObject() {
		return service.getServiceObject();
	}

	@Override
	public void ungetServiceObject() {
		this.service.ungetServiceObject();
	}

}
