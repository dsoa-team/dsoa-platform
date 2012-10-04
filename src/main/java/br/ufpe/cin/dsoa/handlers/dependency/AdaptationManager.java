package br.ufpe.cin.dsoa.handlers.dependency;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.ServiceDescription;
import br.ufpe.cin.dsoa.contract.Slo;
import br.ufpe.cin.dsoa.event.Parameter;
import br.ufpe.cin.dsoa.handlers.dependency.DependencyListener;
import br.ufpe.cin.dsoa.handlers.dependency.impl.ServiceDependency;
import br.ufpe.cin.dsoa.qos.monitoring.service.MonitoringConfiguration;
import br.ufpe.cin.dsoa.qos.monitoring.service.MonitoringConfigurationItem;
import br.ufpe.cin.dsoa.qos.monitoring.service.MonitoringService;
import br.ufpe.cin.dsoa.qos.monitoring.service.events.ErrorEvent;
import br.ufpe.cin.dsoa.qos.monitoring.service.events.Event;
import br.ufpe.cin.dsoa.qos.monitoring.service.events.RequestEvent;
import br.ufpe.cin.dsoa.qos.monitoring.service.events.ResponseEvent;

public class AdaptationManager implements InvocationHandler, DependencyListener {

	private final BundleContext ctx;
	
	private ServiceDependency dependency;
	
	private ServiceReference serviceReference;
	private Object service;


	public AdaptationManager(ServiceDependency dependency) {
		this.dependency = dependency;
		this.ctx = ctx;
	}
	
	
	public synchronized void setSelected(ServiceReference reference) {
		MonitoringService monitor = (MonitoringService) monitorTracker
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
		}
	}

	public synchronized Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		long startTime = System.nanoTime();
		
		List<Parameter> params = new ArrayList<Parameter>();

		for(Class<?> clazz : method.getParameterTypes()){
			params.add(new Parameter(clazz, provider, args[param.size()]));
		}



		Request request = new Request(method.getName(), params);

		InvocationEvent event = Invoacti
		
		Object result = null;
		Long correlationId = random.nextLong();
		sendRequestEvent(correlationId, method, args);
		try {
			if (null != service) {
				result = method.invoke(service, args);
			} else {
				throw new IllegalStateException(
						"Required service is not available!");
			}
		} catch (Exception exception) {
			sendErrorEvent(correlationId, method, exception);
			throw exception;
		}
		sendResponseEvent(correlationId, method, result);
		System.out.println("time : : " + (System.nanoTime() - startTime) / 1000000d);
		return result;
	}

	public void listen(Map result, Object userObject, String statementName) {
		StringBuilder builder = new StringBuilder(1000);
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
				dependency.getSlos(), this, this.blackList);
	}

	private void sendEvent(Event event) {
		MonitoringService monitor = (MonitoringService) monitorTracker
				.getService();
		if (null != monitor) {
			monitor.publishMonitoringEvent(event);
		}
	}

/*	private void sendRequestEvent(Long correlationId, Method method,
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

	public void setSelected(ServiceDescription serviceDescription) {
		// TODO Auto-generated method stub

	}


	public static Object createProxy(ServiceDependency serviceDependency) {
		AdaptationManager manager = new AdaptationManager(serviceDependency);
		Object proxy = Proxy.newProxyInstance(serviceDependency.getSpecification().getClassLoader(),
				new Class[] {serviceDependency.getSpecification()}, manager );

		return proxy;
	}

}
