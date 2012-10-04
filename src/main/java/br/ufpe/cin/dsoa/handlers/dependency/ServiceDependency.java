package br.ufpe.cin.dsoa.handlers.dependency;

import java.util.Collections;
import java.util.List;

import org.apache.felix.ipojo.FieldInterceptor;
import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.contract.Slo;

public class ServiceDependency implements FieldInterceptor {

	/* Handler responsável por tratar as dependências da aplicação */
	private DependencyHandler handler;
	
	/* Proxy que referencia diferentes serviços selecionados de acordo com a qualidade fornecida */
	private Object proxy;
	
	/* Interface do serviço */
	private Class<?> specification;
	
	/* Lista de requisitos não funcionais */
	private List<Slo> slos;
	
	/* Status */
	private boolean valid;


	@SuppressWarnings("rawtypes")
	public ServiceDependency(DependencyHandler handler,
			Class specification, List<Slo> slos) {
		this.handler = handler;
		this.specification = specification;
		this.slos = slos;
		this.valid = false;
	}

	public void start() {
		this.proxy = AdaptationManager.createProxy(this);
	}

	public Object onGet(Object arg0, String arg1, Object arg2) {
		return proxy;
	}

	public void onSet(Object arg0, String arg1, Object arg2) {
	}

	public BundleContext getContext() {
		return handler.getInstanceManager().getContext();
	}

	public Class<?> getSpecification() {
		return specification;
	}

	public List<Slo> getSlos() {
		return Collections.unmodifiableList(slos);
	}

	public String getConsumerPID() {
		return handler.getConsumerPID();
	}

	public String getConsumerName() {
		return handler.getConsumerName();
	}

	public String getQoSMode() {
		return handler.getQosMode();
	}

	public void setValid(boolean stateDep) {
		this.valid = stateDep;
		this.handler.checkValidate();
	}

	public boolean isValid() {
		return valid;
	}

}
