package br.ufpe.cin.dsoa.handlers.dependency;

import java.util.Collections;
import java.util.List;

import org.apache.felix.ipojo.FieldInterceptor;
import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.AdaptationManager;
import br.ufpe.cin.dsoa.contract.Sla;
import br.ufpe.cin.dsoa.contract.Slo;

public class ServiceDependency implements FieldInterceptor {

	/* Handler responsável por tratar as dependências da aplicação */
	private DependencyHandler handler;
	
	/* Dependency manager */
	private DependencyManager manager;
	
	/* SLA */
	private Sla sla;
	
	/* Status */
	private boolean valid;


	public ServiceDependency(DependencyHandler handler, Sla sla) {
		this.handler = handler;
		this.sla = sla;
	}

	public void start() {
		AdaptationManager.manage(this);
	}

	public Object onGet(Object arg0, String arg1, Object arg2) {
		return manager.getProxy();
	}

	public void onSet(Object arg0, String arg1, Object arg2) {
	}

	public BundleContext getContext() {
		return handler.getInstanceManager().getContext();
	}

	public Class<?> getSpecification() {
		return sla.getSpecification();
	}

	public Sla getSla() {
		return sla;
	}

	public String getConsumerPid() {
		return sla.getConsumerPid();
	}

	public String getConsumerName() {
		return sla.getConsumerName();
	}

	public String getQoSMode() {
		return sla.getQosMode();
	}

	public void setValid(boolean valid) {
		this.valid = valid;
		this.handler.checkValidate();
	}

	public boolean isValid() {
		return valid;
	}

	public void setDependencyManager(DependencyManager manager) {
		this.manager = manager;
	}

}
