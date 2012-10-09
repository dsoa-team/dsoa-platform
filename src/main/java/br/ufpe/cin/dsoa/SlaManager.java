package br.ufpe.cin.dsoa;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.Sla;
import br.ufpe.cin.dsoa.contract.Slo;
import br.ufpe.cin.dsoa.event.Request;
import br.ufpe.cin.dsoa.handlers.dependency.DependencyListener;
import br.ufpe.cin.dsoa.monitor.Invocation;
import br.ufpe.cin.dsoa.monitor.MonitoringConfiguration;
import br.ufpe.cin.dsoa.monitor.MonitoringConfigurationItem;
import br.ufpe.cin.dsoa.monitor.Response;

public class SlaManager {

	private static SlaManager instance;

	private ServiceReference serviceReference;
	private DependencyListener listener;
	private Object service;
	private Sla sla;

	private SlaManager() {

	}

	public static SlaManager getInstance() {
		if (instance == null) {
			instance = new SlaManager();
		}
		return instance;
	}

	public Object manage(ServiceReference reference, Sla sla,
			DependencyListener dependencyListener) {

		this.serviceReference = reference;
		this.listener = dependencyListener;
		this.sla = sla;

		this.service = reference.getBundle().getBundleContext()
				.getService(reference);

		/*
		 * this.configuration = new MonitoringConfiguration(sla.getConsumerPid(),
				reference.getProperty("provider.pid").toString(), this);

		for (Slo slo : this.sla.getSlos()) {
			MonitoringConfigurationItem item = new MonitoringConfigurationItem(
					slo.getOperation(), slo.getAttribute(), slo.getExpression()
							.getOperator(), slo.getValue(), slo.getStatistic(),
					slo.getWindowUnit(), slo.getWindowValue(), configuration);
			this.configuration.addConfigurationItem(item);
		}
		monitor.startMonitoring(configuration);
		 */
		
		return new ServiceProxy();
	}

	class ServiceProxy implements InvocationHandler {
		
		public synchronized Object invoke(Object proxy, Method method,
				Object[] args) throws Throwable {

			long startTime = System.nanoTime();
			Request request = new Request(sla.getConsumerPid(),
					serviceReference.getProperty("provider.pid").toString(),
					method.getName(), method.getParameterTypes(), args);

			Response response = null;
			Invocation invocation = new Invocation(request, response);
			Object result = null;
			try {
				if (null != service) {
					// System.out.println("Calling service: ");
					result = method.invoke(service, args);
					// System.out.println("Return: " + result);
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
				// JOGAR NA FILA DO EVENT ADMIN
			}
		}
	}
}