package br.ufpe.cin.dsoa;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.broker.Broker;
import br.ufpe.cin.dsoa.contract.ServiceConsumer;
import br.ufpe.cin.dsoa.contract.ServiceProvider;
import br.ufpe.cin.dsoa.event.InvocationEventOld;

public class AgreementInitiator {

	public static final String PROVIDER_ID = "provider.pid";
	
	/**
	 * Responsável por selecionar o serviço que irá resolver a dependência. Pode utilizar diferentes
	 * políticas ao longo do tempo (uma de cada vez), sendo estas trocadas em tempo de execução.
	 * */
	private Broker broker;

	public List<AgreementTemplate> getTemplates() {
		return null;
	}
	
	public void createSla(final ServiceConsumer consumer, final List<String> blackList, final SlaTemplate sla,
			final SlaMonitor monitor) {
		this.broker.getBestService(sla.getSpecification().getName(),
				sla.getSlos(), new ServiceListener() {
					
					public void setSelected(ServiceReference serviceDescription) {
						String servicePid = service
						new Sla(consumer, new ServiceProvider())
						
					}
				}, blackList);
	}

	public synchronized void setSelected(ServiceReference reference) {
		this.serviceReference = reference;
		this.dependency.setService(slaManager.manage(reference, dependency.getSlaTemplate(), this));
	}
	
	private Object createProxy(ServiceReference reference, SlaTemplate sla,
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
		private SlaTemplate sla;
		private Object service;
		private ServiceReference reference;
		
		public Binding(SlaTemplate sla, ServiceReference reference) {
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

		public SlaTemplate getSla() {
			return sla;
		}

		public void setSla(SlaTemplate sla) {
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