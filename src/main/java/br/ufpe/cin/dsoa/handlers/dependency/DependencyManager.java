package br.ufpe.cin.dsoa.handlers.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.SlaManager;
import br.ufpe.cin.dsoa.broker.Broker;
import br.ufpe.cin.dsoa.broker.impl.BrokerImpl;
import br.ufpe.cin.dsoa.contract.SlaTemplate;
import br.ufpe.cin.dsoa.monitor.MonitoringService;

public class DependencyManager implements DependencyListener,
		ServiceTrackerCustomizer {

	private ServiceDependency dependency;
	private ServiceReference serviceReference;
	private Object service;
	private MonitoringService monitoringService;
	private List<ServiceReference> blackList;
	
	private Broker broker;
	private SlaManager slaManager;

	public DependencyManager(ServiceDependency dependency) {
		this.dependency = dependency;
		this.broker = new BrokerImpl(dependency.getContext());
		this.blackList = new ArrayList<ServiceReference>();
		this.slaManager = new SlaManager();
		this.broker.getBestService(dependency.getSpecification().getName(),
				dependency.getSla().getSlos(), this, this.blackList);
		
	}

	public Object getProxy() {
		return this.service;
	}

	public BundleContext getContext() {
		return dependency.getContext();
	}

	public String getProviderId() {
		return (String) this.serviceReference.getProperty("provider.pid");
	}

	public String getConsumerId() {
		return this.dependency.getConsumerPid();
	}

	public synchronized void setSelected(ServiceReference reference) {
		this.serviceReference = reference;
		this.service = slaManager.manage(reference, dependency.getSla(), this);

		dependency.setValid(true);
	}

	public void listen(Map result, Object userObject, String statementName) {
		/*
		 * StringBuilder builder = new StringBuilder(1000);
		 * builder.append("Violacao").append(" - ");
		 * builder.append(System.currentTimeMillis()).append(" - ");
		 * builder.append(result.get("value")).append(" - ");
		 * builder.append(statementName); logGenerator.log(Level.INFO,
		 * builder.toString());
		 */

		Planner planner = Planner.getInstance();
		planner.plan(result, userObject, statementName, dependency.getSla());

		this.blackList.add(this.serviceReference);
		this.service = null;
		this.dependency.getContext().ungetService(this.serviceReference);
		this.serviceReference = null;
		this.dependency.setValid(false);

		// System.out.println("A monitoring event occurred: " + statementName);
		// System.out.println("\tClause: " + ((MonitoringConfigurationItem)
		// userObject).getStatement());
		// for (Object key : result.keySet()) {
		// System.out.println("\tKey: " + key);
		// System.out.println("\tValue: " + result.get(key));
		// System.out.println("");
		// System.out.println("");
		// }

		// linha adicionadas para considerar efetivas trocas
		// this.blackList.removeAll(blackList);

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

	public Object addingService(ServiceReference reference) {
		this.monitoringService = (MonitoringService) this.getContext()
				.getService(reference);
		return this.monitoringService;
	}

	public void modifiedService(ServiceReference reference, Object service) {

	}

	public void removedService(ServiceReference reference, Object service) {
		this.monitoringService = null;
		getContext().ungetService(reference);
	}

	static class Planner {

		private static Planner instance;

		private Planner() {
		}

		public static Planner getInstance() {
			if (instance == null) {
				instance = new Planner();
			}

			return instance;
		}

		public void plan(Map<?,?> result, Object userObject, String statementName,
				SlaTemplate sla) {
			// TODO Auto-generated method stub

		}

	}
}
