package br.ufpe.cin.dsoa.handlers.dependency;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.broker.Broker;
import br.ufpe.cin.dsoa.broker.impl.BrokerImpl;
import br.ufpe.cin.dsoa.event.Request;
import br.ufpe.cin.dsoa.monitor.Invocation;
import br.ufpe.cin.dsoa.monitor.Response;

public class DependencyManager implements InvocationHandler, DependencyListener {

	private final BundleContext ctx;
	
	private ServiceDependency dependency;
	
	private ServiceReference serviceReference;
	private Object service;
	
	private Broker broker;
	private List<ServiceReference> blackList;

	public DependencyManager(ServiceDependency dependency) {
		this.dependency = dependency;
		this.ctx = dependency.getContext();
		this.broker = new BrokerImpl(ctx);
		this.blackList = new ArrayList<ServiceReference>();
		this.broker.getBestService(dependency.getSpecification().getName(),
				dependency.getSlos(), this, this.blackList);
	}
	
	public String getProviderId() {
		return (String)this.serviceReference.getProperty("provider.pid");
	}
	
	public String getConsumerId() {
		return this.dependency.getConsumerPID();
	}
	
	public synchronized void setSelected(ServiceReference reference) {
		this.serviceReference = reference;
		this.service = ctx.getService(reference);
		dependency.setValid(true);
		/*MonitoringService monitor = (MonitoringService) monitorTracker
				.getService();
		if (monitor != null) {
			this.serviceReference = reference;
			this.service = ctx.getService(reference);

			this.configuration = new MonitoringConfiguration(
					dependency.getConsumerPID(), reference.getProperty(
							"provider.pid").toString(), this);

			for (Slo slo : this.dependency.getSlos()) {
				MonitoringConfigurationItem item = new MonitoringConfigurationItem(
						slo.getOperation(), slo.getAttribute(), slo
								.getExpression().getOperator(), slo.getValue(),
						slo.getStatistic(), slo.getWindowUnit(),
						slo.getWindowValue(), configuration);
				this.configuration.addConfigurationItem(item);
			}
			monitor.startMonitoring(configuration);
			dependency.setValid(true);
		}*/
	}

	public synchronized Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
 		long startTime = System.nanoTime();
		Request request = new Request(this.getConsumerId(), this.getProviderId(), method.getName(), method.getParameterTypes(), args);
		Response response = null;
		Invocation invocation = new Invocation(request, response);
		Object result = null;
		try {
			if (null != service) {
				//System.out.println("Calling service: ");
				result = method.invoke(service, args);
				//System.out.println("Return: " + result);
			} else {
				throw new IllegalStateException(
						"Required service is not available!");
			}
			response = new Response(method.getReturnType(), result);
			return result;
		} catch (Exception exception) {
			response = new Response(exception);
			throw exception;
		} finally {
			
		}
	}

	public void listen(Map result, Object userObject, String statementName) {
		/*StringBuilder builder = new StringBuilder(1000);
		builder.append("Violacao").append(" - ");
		builder.append(System.currentTimeMillis()).append(" - ");
		builder.append(result.get("value")).append(" - ");
		builder.append(statementName);
		logGenerator.log(Level.INFO, builder.toString());

		this.blackList.add(this.serviceReference);
		this.service = null;
		this.ctx.ungetService(this.serviceReference);
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
				dependency.getSlos(), this, this.blackList);*/
	}



/*	
 
 	private void sendEvent(Event event) {
		MonitoringService monitor = (MonitoringService) monitorTracker
				.getService();
		if (null != monitor) {
			monitor.publishMonitoringEvent(event);
		}
	}
 
  	private void sendRequestEvent(Long correlationId, Method method,
			Object[] args) {
		sendEvent(new RequestEvent(System.nanoTime(),
				configuration.getClientId(), configuration.getServiceId(),
				correlationId.toString(), method.getName(), null, null, args));

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
		StringBuilder builder = new StringBuilder(1000);
		builder.append("Request").append(" - ");
		// builder.append(df.format(new
		// Date(System.currentTimeMillis()))).append(" - ");
		builder.append(System.currentTimeMillis()).append(" - ");
		builder.append(correlationId.toString()).append(" - ");
		builder.append(configuration.getClientId()).append(" - ");
		builder.append(configuration.getServiceId()).append(" - ");
		builder.append(method.getName());
		logGenerator.log(Level.INFO, builder.toString());

	}

	private void sendResponseEvent(Long correlationId, Method method,
			Object result) {
		sendEvent(new ResponseEvent(System.nanoTime(),
				configuration.getClientId(), configuration.getServiceId(),
				correlationId.toString(), method.getName(), null, result));
	}

	private void sendErrorEvent(Long correlationId, Method method,
			Exception exception) {
		Throwable rootCause = exception;
		Throwable cause = rootCause.getCause();

		while (cause != null) {
			rootCause = cause;
			cause = rootCause.getCause();
		}

		sendEvent(new ErrorEvent(System.nanoTime(),
				configuration.getClientId(), configuration.getServiceId(),
				correlationId.toString(), method.getName(), rootCause
						.getClass().getName(), rootCause.getMessage()));

		// System.out.println("#$:  " + rootCause.getClass().getName());

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
		StringBuilder builder = new StringBuilder(1000);
		builder.append("Error").append(" - ");
		// builder.append(df.format(new
		// Date(System.currentTimeMillis()))).append(" - ");
		builder.append(System.currentTimeMillis()).append(" - ");
		builder.append(correlationId.toString()).append(" - ");
		builder.append(configuration.getClientId()).append(" - ");
		builder.append(configuration.getServiceId()).append(" - ");
		builder.append(method.getName());
		logGenerator.log(Level.INFO, builder.toString());

	}*/

	public static Object createProxy(ServiceDependency serviceDependency) {
		DependencyManager manager = new DependencyManager(serviceDependency);
		Object proxy = Proxy.newProxyInstance(serviceDependency.getSpecification().getClassLoader(),
				new Class[] {serviceDependency.getSpecification()}, manager );

		return proxy;
	}
	
}
