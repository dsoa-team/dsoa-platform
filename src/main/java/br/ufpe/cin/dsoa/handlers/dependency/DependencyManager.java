package br.ufpe.cin.dsoa.handlers.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.SlaManager;
import br.ufpe.cin.dsoa.contract.Aggreement;
import br.ufpe.cin.dsoa.contract.AggreementMonitor;
import br.ufpe.cin.dsoa.contract.AggreementOffer;

public class DependencyManager implements AggreementMonitor {

	
	/**
	 * Responsável por criar e remover 'contratos' entre o cliente e o provedor de serviço.
	 */
	private SlaManager slaManager;
	
	/**
	 * Represents a service dependency which is dynamically resolved based on functional and non-functional requirements 
	 */
	private ServiceDependency dependency;
	
	private Aggreement aggreement;

	/**
	 * Lista de serviços utilizados anteriormente e descartados em virtude de não terem atendido adquadamente.
	 */
	private List<String> blackList;
	
	public DependencyManager(ServiceDependency dependency) {
		this.dependency = dependency;
		this.blackList = new ArrayList<String>();
	}
	
	public void start() {
		this.aggreement = this.slaManager.createSla(dependency.getConsumer(), dependency.getSlaTemplate());
	}
	
	public void listen(Map result, Object userObject, String statementName) {
		this.aggreement.terminate();
	}

	class Planner {

		public void plan(Map<?,?> result, Object userObject, String statementName,
				AggreementOffer sla) {
			// TODO Auto-generated method stub

		}

	}
}
