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
	 * Representa uma dependência de um serviço que é resolvida dinamicamente com base nos requisitos
	 * funcionais e não funcionais especificados no arquivo de configuração. 
	 */
	private ServiceDependency dependency;
	
	/**
	 * Referência para o serviço que é momentaneamente apontado pela dependência. Esse apontador é trocado
	 * dinamicamente (em tempo de execução) caso o serviço deixe de atender aos requisitos não funcionais
	 * estabelecidos.
	 */
	private ServiceReference serviceReference;
	
	private Object service;
	
	/**
	 * Lista de serviços utilizados anteriormente e descartados em virtude de não terem atendido adquadamente.
	 */
	private List<ServiceReference> blackList;
	
	/**
	 * Responsável por selecionar o serviço que irá resolver a dependência. Pode utilizar diferentes
	 * políticas ao longo do tempo (uma de cada vez), sendo estas trocadas em tempo de execução.
	 * */
	private Broker broker;
	
	/**
	 * Responsável por criar e remover 'contratos' entre o cliente e o provedor de serviço.
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
