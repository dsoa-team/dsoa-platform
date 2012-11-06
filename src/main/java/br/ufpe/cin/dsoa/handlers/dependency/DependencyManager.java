package br.ufpe.cin.dsoa.handlers.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.SlaManager;
import br.ufpe.cin.dsoa.broker.Broker;
import br.ufpe.cin.dsoa.broker.impl.BrokerImpl;
import br.ufpe.cin.dsoa.contract.Sla;
import br.ufpe.cin.dsoa.monitor.SlaListener;

public class DependencyManager implements ServiceListener, SlaListener {

	/**
	 * Represents a service dependency which is dynamically resolved based on functional and non-functional requirements 
	 */
	private ServiceDependency dependency;
	
	
	private ServiceReference serviceReference;

	
	/**
	 * Lista de servi�os utilizados anteriormente e descartados em virtude de n�o terem atendido adquadamente.
	 */
	private List<ServiceReference> blackList;
	
	/**
	 * Respons�vel por selecionar o servi�o que ir� resolver a depend�ncia. Pode utilizar diferentes
	 * pol�ticas ao longo do tempo (uma de cada vez), sendo estas trocadas em tempo de execu��o.
	 * */
	private Broker broker;
	
	/**
	 * Respons�vel por criar e remover 'contratos' entre o cliente e o provedor de servi�o.
	 */
	private SlaManager slaManager;

	public DependencyManager(ServiceDependency dependency) {
		this.dependency = dependency;
		this.blackList = new ArrayList<ServiceReference>();
		
		this.broker.getBestService(dependency.getSla().getSpecification().getName(),
				dependency.getSla().getSlos(), this, this.blackList);
	}
	
	public BundleContext getContext() {
		return dependency.getContext();
	}

	public synchronized void setSelected(ServiceReference reference) {
		this.serviceReference = reference;
		this.dependency.setService(slaManager.manage(reference, dependency.getSla(), this));
	}

	public void listen(Map result, Object userObject, String statementName) {

		this.blackList.add(this.serviceReference);
		this.dependency.getContext().ungetService(this.serviceReference);
		this.serviceReference = null;
		this.dependency.setValid(false);

		broker.getBestService(dependency.getSla().getSpecification().getName(),
				dependency.getSla().getSlos(), this, this.blackList);
	}

	class Planner {

		public void plan(Map<?,?> result, Object userObject, String statementName,
				Sla sla) {
			// TODO Auto-generated method stub

		}

	}
}
