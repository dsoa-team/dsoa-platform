package br.ufpe.cin.dsoa.handlers.dependency;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.broker.Broker;

public class DependencyManager implements InvocationHandler, DependencyListener {

	private final BundleContext ctx;
	
	private ServiceReference serviceReference;
	private Object service;
	private Broker broker;
	private List<ServiceReference> blackList;

	public DependencyManager(ServiceDependency dependency) {
		this.ctx = dependency.getContext();
		this.broker.getBestService(dependency.getSpecification().getName(),
				dependency.getSlos(), this, this.blackList);
	}
	
	
	public synchronized void setSelected(ServiceReference reference) {
		this.serviceReference = reference;
		this.service = ctx.getService(reference);
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
		
		
/*		
 		long startTime = System.nanoTime();
 		List<Parameter> params = new ArrayList<Parameter>();

		for(Class<?> clazz : method.getParameterTypes()){
			params.add(new Parameter(clazz, provider, args[param.size()]));
		}

		Request request = new Request(method.getName(), params);

		InvocationEvent event = Invoacti
		
		
		
		sendRequestEvent(correlationId, method, args);*/
		//Long correlationId = random.nextLong();
		System.out.println("FUNCIONOU...");
		System.out.println("Method: " + method.getName());
		System.out.println("Parameters: " + args);
		Object result = null;
		try {
			if (null != service) {
				System.out.println("Calling service: ");
				result = method.invoke(service, args);
				System.out.println("Return: " + result);
			} else {
				throw new IllegalStateException(
						"Required service is not available!");
			}
		} catch (Exception exception) {
			//sendErrorEvent(correlationId, method, exception);
			throw exception;
		}
		return result;
/*		sendResponseEvent(correlationId, method, result);
		System.out.println("time : : " + (System.nanoTime() - startTime) / 1000000d);
		return result;*/
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
