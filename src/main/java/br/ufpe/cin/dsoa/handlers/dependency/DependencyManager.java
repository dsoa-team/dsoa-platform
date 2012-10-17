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
import br.ufpe.cin.dsoa.monitor.MonitoringListener;

public class DependencyManager implements DependencyListener, MonitoringListener {

	/**
	 * Representa uma depend�ncia de um servi�o que � resolvida dinamicamente com base nos requisitos
	 * funcionais e n�o funcionais especificados no arquivo de configura��o. 
	 */
	private ServiceDependency dependency;
	
	/**
	 * Refer�ncia para o servi�o que � momentaneamente apontado pela depend�ncia. Esse apontador � trocado
	 * dinamicamente (em tempo de execu��o) caso o servi�o deixe de atender aos requisitos n�o funcionais
	 * estabelecidos.
	 */
	private ServiceReference serviceReference;
	
	private Object service;
	
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
		
		this.slaManager = new SlaManager();
		this.broker = new BrokerImpl(dependency.getContext());
		this.broker.getBestService(dependency.getSpecification().getName(),
				dependency.getSla().getSlos(), this, this.blackList);
		
	}
	
	public BundleContext getContext() {
		return dependency.getContext();
	}

	public Object getService() {
		return getContext().getService(serviceReference);
	}

	public synchronized void setSelected(ServiceReference reference) {
		this.serviceReference = reference;
		this.service = this.slaManager.manage(reference, dependency.getSla(), this);
		this.dependency.setValid(true);
	}

	public void listen(Map result, Object userObject, String statementName) {

		Planner planner = Planner.getInstance();
		planner.plan(result, userObject, statementName, dependency.getSla());

		this.blackList.add(this.serviceReference);
		this.dependency.getContext().ungetService(this.serviceReference);
		this.serviceReference = null;
		this.dependency.setValid(false);

		broker.getBestService(dependency.getSpecification().getName(),
				dependency.getSla().getSlos(), this, this.blackList);
	}

	/*
	 * 
	 * private void sendEvent(Event event) { MonitoringService monitor =
	 * (MonitoringService) monitorTracker .getService(); if (null != monitor) {
	 * monitor.publishMonitoringEvent(event); } }
	 * 
	 * private void sendRequestEvent(Long correlationId, Method method, Object[]
	 * args) { sendEvent(new RequestEvent(System.nanoTime(),
	 * configuration.getClientId(), configuration.getServiceId(),
	 * correlationId.toString(), method.getName(), null, null, args));
	 * 
	 * DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
	 * StringBuilder builder = new StringBuilder(1000);
	 * builder.append("Request").append(" - "); // builder.append(df.format(new
	 * // Date(System.currentTimeMillis()))).append(" - ");
	 * builder.append(System.currentTimeMillis()).append(" - ");
	 * builder.append(correlationId.toString()).append(" - ");
	 * builder.append(configuration.getClientId()).append(" - ");
	 * builder.append(configuration.getServiceId()).append(" - ");
	 * builder.append(method.getName()); logGenerator.log(Level.INFO,
	 * builder.toString());
	 * 
	 * }
	 * 
	 * private void sendResponseEvent(Long correlationId, Method method, Object
	 * result) { sendEvent(new ResponseEvent(System.nanoTime(),
	 * configuration.getClientId(), configuration.getServiceId(),
	 * correlationId.toString(), method.getName(), null, result)); }
	 * 
	 * private void sendErrorEvent(Long correlationId, Method method, Exception
	 * exception) { Throwable rootCause = exception; Throwable cause =
	 * rootCause.getCause();
	 * 
	 * while (cause != null) { rootCause = cause; cause = rootCause.getCause();
	 * }
	 * 
	 * sendEvent(new ErrorEvent(System.nanoTime(), configuration.getClientId(),
	 * configuration.getServiceId(), correlationId.toString(), method.getName(),
	 * rootCause .getClass().getName(), rootCause.getMessage()));
	 * 
	 * // System.out.println("#$:  " + rootCause.getClass().getName());
	 * 
	 * DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
	 * StringBuilder builder = new StringBuilder(1000);
	 * builder.append("Error").append(" - "); // builder.append(df.format(new //
	 * Date(System.currentTimeMillis()))).append(" - ");
	 * builder.append(System.currentTimeMillis()).append(" - ");
	 * builder.append(correlationId.toString()).append(" - ");
	 * builder.append(configuration.getClientId()).append(" - ");
	 * builder.append(configuration.getServiceId()).append(" - ");
	 * builder.append(method.getName()); logGenerator.log(Level.INFO,
	 * builder.toString());
	 * 
	 * }
	 */

	class Planner {

		public void plan(Map<?,?> result, Object userObject, String statementName,
				Sla sla) {
			// TODO Auto-generated method stub

		}

	}
}
