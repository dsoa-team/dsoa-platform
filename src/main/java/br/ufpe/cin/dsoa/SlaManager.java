package br.ufpe.cin.dsoa;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.Sla;
import br.ufpe.cin.dsoa.event.InvocationEvent;
import br.ufpe.cin.dsoa.event.Request;
import br.ufpe.cin.dsoa.event.Response;
import br.ufpe.cin.dsoa.handlers.dependency.DependencyListener;
import br.ufpe.cin.dsoa.monitor.MonitoringService;

public class SlaManager {

	private DependencyListener listener;
	private Sla sla;

	
	
	public Object manage(ServiceReference reference, Sla sla,
			DependencyListener dependencyListener) {

		this.listener = dependencyListener;
		this.sla = sla;
		this.sla.setServiceReference(reference);
		return new ServiceProxy();
	}

	class ServiceProxy implements InvocationHandler {
		
		public synchronized Object invoke(Object proxy, Method method,
				Object[] args) throws Throwable {

			long startTime = System.nanoTime();
			Request request = new Request(sla.getConsumerPid(),
					sla.getServiceReference().getProperty("provider.pid").toString(),
					method.getName(), method.getParameterTypes(), args);

			Response response = null;
			InvocationEvent invocation = new InvocationEvent(request, response);
			Object result = null;
			try {
				Object service = sla.getService();
				if (null != service) {
					result = method.invoke(service, args);
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
}