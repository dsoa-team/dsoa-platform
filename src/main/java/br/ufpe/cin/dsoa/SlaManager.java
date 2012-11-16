package br.ufpe.cin.dsoa;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.Sla;
import br.ufpe.cin.dsoa.event.InvocationEventOld;
import br.ufpe.cin.dsoa.monitor.SlaListener;

public class SlaManager {

	public static final String PROVIDER_ID = "provider.pid";
	
	// private ServiceRegistry<MonitoringService> registry;

	public Object manage(ServiceReference reference, Sla sla,
			SlaListener listener) {
		return createProxy(reference, sla, listener);
	}

	private Object createProxy(ServiceReference reference, Sla sla,
			SlaListener listener) {
		Binding binding = new Binding(sla, reference);
		return new ServiceProxy(binding);
	}

	class ServiceProxy implements InvocationHandler {

		/*
		 * String attribute; MonitoringService monitoringService;
		 * 
		 * List<MonitoringService> monitoringServices = new
		 * ArrayList<MonitoringService>(); List<Slo> slos = sla.getSlos(); for
		 * (Slo slo : slos) { attribute = slo.getAttribute(); monitoringService
		 * = registry.getService(attribute); if (monitoringService != null) {
		 * monitoringServices.add(monitoringService); } } return new
		 * ServiceProxy(monitoringServices);
		 */

		private Binding binding;

		public ServiceProxy(Binding binding) {
			this.binding = binding;
		}

		public synchronized Object invoke(Object proxy, Method method,
				Object[] args) throws Throwable {

			long startTime = System.nanoTime();

			Object result = null;
			Exception exception = null;
			boolean success = true;
			if (null != binding) {
				try {
					result = method.invoke(binding.getService(), args);
					return result;
				} catch (Exception exc) {
					success = false;
					exception = exc;
					throw exc;
				} finally {
					InvocationEventOld invocation = new InvocationEventOld(binding.getConsumerId(), binding.getServiceId(),							
							method.getName(), method.getParameterTypes(), args,
							method.getReturnType(), result, success, exception);
					//publishEvent(invocation);
				}
			} else {
				throw new IllegalStateException(
						"Required service is not available!");
			}
		}
	}
	
	class Binding {
		
		private String serviceId;
		private String consumerId;
		private Sla sla;
		private Object service;
		private ServiceReference reference;
		
		public Binding(Sla sla, ServiceReference reference) {
			super();
			this.sla = sla;
			this.reference = reference;
			this.service = reference.getBundle().getBundleContext().getService(reference);
			this.serviceId = (String) reference.getProperty(PROVIDER_ID);
		}

		public String getServiceId() {
			return serviceId;
		}

		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}

		public String getConsumerId() {
			return consumerId;
		}

		public void setConsumerId(String consumerId) {
			this.consumerId = consumerId;
		}

		public Sla getSla() {
			return sla;
		}

		public void setSla(Sla sla) {
			this.sla = sla;
		}

		public Object getService() {
			return service;
		}

		public void setService(Object service) {
			this.service = service;
		}

		public ServiceReference getReference() {
			return reference;
		}

		public void setReference(ServiceReference reference) {
			this.reference = reference;
		}
	}
}