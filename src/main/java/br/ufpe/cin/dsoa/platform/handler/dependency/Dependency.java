package br.ufpe.cin.dsoa.platform.handler.dependency;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.ipojo.FieldInterceptor;

import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.platform.handler.dependency.manager.DependencyManager;
import br.ufpe.cin.dsoa.platform.monitor.ProxyFactory;

public class Dependency implements FieldInterceptor, Service {

	private DependencyHandler 			handler;
	
	private ServiceSpecification		requiredSpecification;
	
	private Service						service;
	
	private List<String>		 		blackList;
	
	private String componentId;
	
	private boolean valid;
	
	private DependencyManager manager;

	private ProxyFactory dynamicProxy;

	public Dependency(DependencyHandler dependencyHandler, String componentId, ServiceSpecification specification) {
		super();
		this.handler = dependencyHandler;
		this.componentId = componentId;
		this.requiredSpecification = specification;
		this.blackList = new ArrayList<String>();
		this.valid = false;
		this.manager = new DependencyManager(this);
	}
	
	public void setProxy(ProxyFactory dynamicProxyFactory){
		this.dynamicProxy = dynamicProxyFactory;
	}

	public void start() {
		manager.start();
	}

	public void stop() {
		manager.stop();
	}
	
	public boolean isValid() {
		return this.valid;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
		if (valid) {
			handler.computeState();
		} else {
			handler.setValidity(false);
		}
	}
	
	public void setService(Service service) {
		if (service != this.service && this.service != null){
			this.service.ungetServiceObject();
		}
		this.service = service;
	}
	
	public String getComponentId() {
		return componentId;
	}
	
	public DependencyHandler getHandler() {
		return handler;
	}

	public ServiceSpecification getSpecification() {
		return this.requiredSpecification;
	}
	
	public List<String> getBlackList() {
		return blackList;
	}
	
	public Service getService() {
		return this.service;
	}

	public void onSet(Object pojo, String fieldName, Object value) {
		// Just do nothing...
	}

	public Object onGet(Object pojo, String fieldName, Object value) {
		return dynamicProxy.getProxy(componentId, this);
	}

	@Override
	public String getProviderId() {
		String result = null;
		if (this.service != null) {
			result = this.service.getProviderId();
		}
		return result;
	}

	@Override
	public Dictionary<?, ?> getProperties() {
		Dictionary<?, ?> result = null;
		if (this.service != null) {
			result = this.service.getProperties();
		}
		return result;
	}

	@Override
	public Object getServiceObject() {
		Object result = null;
		if (this.service != null) {
			result = this.service.getServiceObject();
		}
		return result;
	}

	@Override
	public void ungetServiceObject() {
		
	}

}
