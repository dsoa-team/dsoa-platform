package br.ufpe.cin.dsoa.platform.handler.requires;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.attribute.AttributableId;
import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventPropertyMapper;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventDistribuitionService;
import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.FilterExpression;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.service.Binding;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.RelationalOperator;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.api.service.impl.ServiceInstanceProxy;
import br.ufpe.cin.dsoa.api.service.impl.ServiceInstanceProxyImpl;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.attribute.ConstraintViolationListener;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringRegistration;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.DsoaUtil;

/**
 * This class implements an autonomic binding manager, which is responsible for managing a Binding meta-object.
 * In terms of taxonomy, the BindingManager represents a Reflective Computational Element, while the Binding
 * represents a Reflective Meta-data Element (vide Forms) 
 * 
 * @author fabions
 */
public class DsoaBindingManager implements ConstraintViolationListener, ServiceListener {
	

	private static Logger logger;
	
	{
		java.util.logging.Formatter f = new java.util.logging.Formatter() {

			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}
		};
		logger = Logger.getLogger("BindingLogger");
		try {
			FileHandler adaptationLogFile = new FileHandler(DsoaUtil.getLoggerName("Binding"));
			adaptationLogFile.setFormatter(f);
			logger.addHandler(adaptationLogFile);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	/** 
	 * The meta-object representing a binding between the field and a ServiceInstance 
	 */
	private Binding binding;

	/**
	 * The list of undesirable services (represented by their ids)
	 */
	private List<String> blackList;	
	
	private DsoaPlatform dsoaPlatform;
	
	private List<MonitoringRegistration> monitoringRegistrations;

	private DsoaProxyHandler proxyHandler;
	

	public DsoaBindingManager(DsoaPlatform dsoa, Binding binding) {
		super();
		this.blackList = new ArrayList<String>();
		this.dsoaPlatform = dsoa;
		this.binding = binding;
	}
	
	public List<String> getBlackList() {
		return blackList;
	}
	
	/**
	 * This is the part of the management interface that is required by the Binding
	 * meta-object implementation (model@runtime) 
	 */
	public void selectService() {
		dsoaPlatform.getServiceRegistry().getBestService(binding.getPort().getServiceSpecification(),
				getBlackList(), this);
	}
	
	public void startMonitoring(String serviceId) {

		this.monitoringRegistrations = new ArrayList<MonitoringRegistration>();

		for (final Constraint constraint : binding.getPort()
				.getServiceSpecification().getNonFunctionalSpecification()
				.getConstraints()) {
			String operationName = constraint.getOperation();
			AttributableId attributableId = new AttributableId(serviceId, operationName);
			String attributeId = constraint.getAttributeId();
			final AttributeEventMapper attMapper = this.dsoaPlatform.getAttEventMapperCatalog()
					.getAttributeEventMapper(attributeId);

			if (attMapper != null) {

				EventType eventType = attMapper.getEventType();
				PropertyType sourceType = eventType
						.getMetadataPropertyType(Constants.EVENT_SOURCE);

				List<FilterExpression> filterList = new ArrayList<FilterExpression>();

				// Filter source
				FilterExpression filterExp = new FilterExpression(new Property(
						attributableId.getId(), sourceType),
						RelationalOperator.EQ);
				filterList.add(filterExp);

				List<AttributeEventPropertyMapper> propertyMappers = attMapper
						.getData();

				// Filter constraints
				for (AttributeEventPropertyMapper propertyMapper : propertyMappers) {
					String eventPropertyName = propertyMapper.getExpression()
							.replaceFirst(attMapper.getEventAlias() + ".", "")
							.replaceFirst("data.", "");

					PropertyType propertyType = eventType
							.getDataPropertyType(eventPropertyName);
					filterExp = new FilterExpression(new Property(
							constraint.getThreashold(), propertyType),
							constraint.getExpression().getComplement());
					filterList.add(filterExp);
				}

				EventFilter filter = new EventFilter(filterList);
				Subscription subscription = new Subscription(eventType, filter);

				EventConsumer consumer = new EventConsumerImpl(
						attMapper, constraint);

				this.monitoringRegistrations.add(new MonitoringRegistration(
						consumer, subscription));
				this.dsoaPlatform.getEpService()
						.subscribe(consumer, subscription, true);// TODO:
																	// parametrizar
			}
		}
	}
	
	public void stopMonitoring() {
		if(this.monitoringRegistrations != null && !this.monitoringRegistrations.isEmpty()) {
			for(MonitoringRegistration registration : this.monitoringRegistrations) {
				this.dsoaPlatform.getEpService().unsubscribe(registration.getConsumer(), registration.getSubscription());
			}
		}
		this.monitoringRegistrations = null; 
	}

	/**
	 * These are callback methods used by the Service Registry to notify about
	 * availability of required service
	 */
	// TODO VER REMOVER SYNC
	public synchronized void onArrival(ServiceInstance serviceInstance) {
		((ServiceInstanceProxyImpl)serviceInstance).setServiceObject(this.getProxy(binding.getName(), serviceInstance));
		binding.bind((ServiceInstanceProxy)serviceInstance);
	}

	// VER REMOVER SYNC
	public synchronized void onDeparture(ServiceInstance service) {
		binding.unbind();
		blackList.clear();//TODO:REMOVE
		if (proxyHandler != null) {
			proxyHandler.ungetService();
			proxyHandler = null;
		}
		selectService();
	}
	
	public void onError(Exception e) {
	}

	@Override
	public void constraintViolated(String serviceId, Constraint constraint,
			AttributeValue value) {
			this.evaluate(serviceId, constraint, value);
	}
	
	public void evaluate(String serviceId, Constraint constraint, AttributeValue value) {
		System.err.println("====================================================");
		System.err.println("ServiceId: " + serviceId);
		String op1 = constraint.getAttributeId();
		if (constraint.getOperation() != null) {
			op1 = "." + constraint.getOperation();
		}
		String expected = constraint.getExpression().renderExpression(op1,
				constraint.getThreashold() + "");
		System.err.println(String.format("Expected value= %s :: Monitored Value= %s", expected,
				value.getValue()));

		System.err.println("====================================================");
		
		this.binding.unbind();
		this.selectService();
	}


	private void notify(String eventTypeName, String serviceId, String consumerId) {
		// TODO Assert that the instance is always not null whenever this method
		// is called.
		String serviceInterface = binding.getPort().getServiceSpecification()
				.getFunctionalInterface().getInterfaceName();

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Constants.SERVICE_ID, serviceId);
		data.put(Constants.CONSUMER_ID, consumerId);
		data.put(Constants.SERVICE_INTERFACE, serviceInterface);

		Map<String, Object> metadata = new HashMap<String, Object>();
		// TODO BY NOW, THE Binding name is set to required field name
		metadata.put(Constants.EVENT_SOURCE,
				String.format("%s.%s", binding.getComponentInstance().getName(), binding.getName()));

		dsoaPlatform.getEventDistribuitionService().postEvent(
				eventTypeName, metadata, data);
		logger.info("Binding notification: " + eventTypeName + " at "
				+ System.currentTimeMillis() + "," + consumerId + ":"
				+ serviceId);
	}

	private void notifyUnbind(String serviceId, String consumerId) {
		this.notify(Constants.UNBIND_EVENT, serviceId, consumerId);
	}

	public void notifyBind(String serviceId, String consumerId) {
		this.notify(Constants.BIND_EVENT, serviceId, consumerId);
	}

	private class EventConsumerImpl implements EventConsumer {
		
		private AttributeEventMapper attMapper;
		private Constraint constraint;
		
		public EventConsumerImpl(AttributeEventMapper attMapper, Constraint constraint) {
			this.attMapper = attMapper;
			this.constraint = constraint;
		}

		@Override
		public void handleEvent(Event event) {

			AttributeValue value = attMapper.convertToAttribute(event);
			logger.info(value.getAttribute().getId() + "," + value.getValue());
			constraintViolated(binding.getServiceInstanceProxy().getName(), constraint, value);
		}

		@Override
		public String getComponentInstanceName() {
			return binding.getComponentInstance().getName();
		}
	}

	public void bound(String serviceId) {
		this.startMonitoring(serviceId);
		this.notifyBind(serviceId, binding.getComponentInstance().getName());
	}

	public void unbound(String serviceId) {
		this.notifyUnbind(serviceId, binding.getComponentInstance().getName());
	}
	
	
	
	

	
	private EventDistribuitionService distribuitionService;
	
	public Object getProxy(String consumerId, ServiceInstance serviceInstance) {
		if (this.proxyHandler == null) {
			this.proxyHandler = new DsoaProxyHandler(consumerId, serviceInstance);
			
			ClassLoader cl = this.getClass().getClassLoader();
			String itfClassname = serviceInstance.getPort().getServiceSpecification().getFunctionalInterface().getInterfaceName();
			Class<?> itfClass;
			try {
				itfClass = cl.loadClass(itfClassname);
				Object proxy = java.lang.reflect.Proxy.newProxyInstance(cl,
						new Class[] { itfClass }, proxyHandler);
				return proxy;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return this.proxyHandler;
	}

	class DsoaProxyHandler implements InvocationHandler {

		private String consumerId;
		private String serviceId;
		
		private Object serviceObject;
		private ServiceReference serviceReference;

		/**
		 * HashCode method.
		 */
		private Method m_hashCodeMethod;

		/**
		 * Equals method.
		 */
		private Method m_equalsMethod;

		/**
		 * toStirng method.
		 */
		private Method m_toStringMethod;

		public DsoaProxyHandler(String consumerId, ServiceInstance serviceInstance) {
			this.consumerId = consumerId;
			this.serviceId = serviceInstance.getName();
			this.serviceReference = ((ServiceInstanceProxyImpl)serviceInstance).getServiceReference();
			try {
				m_hashCodeMethod = Object.class.getMethod("hashCode", null);
				m_equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
				m_toStringMethod = Object.class.getMethod("toString", null);
			} catch (NoSuchMethodException e) {
				throw new NoSuchMethodError(e.getMessage());
			}
			
		}
		
		public void ungetService() {
			if (this.serviceReference != null) {
				serviceReference.getBundle().getBundleContext().ungetService(serviceReference);
			}
			this.serviceObject = null;
		}

		public synchronized Object getServiceObject() {
			if (this.serviceObject == null && 
					null != serviceReference) {
				serviceObject = serviceReference.getBundle().getBundleContext()
						.getService(serviceReference);
			}
			return serviceObject;
		}
		
		
		/**
		 * Invocation Handler delegating invocation on the dependency object.
		 * 
		 * @param proxy
		 *            the proxy object
		 * @param method
		 *            the method
		 * @param args
		 *            the arguments
		 * @return a proxy object.
		 * @throws Throwable
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
		 *      java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Class<?> declaringClass = method.getDeclaringClass();
			String exceptionMessage = null;
			String exceptionClassName = null;

			if (declaringClass == Object.class) {
				if (method.equals(m_hashCodeMethod)) {
					return new Integer(this.hashCode());
				} else if (method.equals(m_equalsMethod)) {
					return proxy == args[0] ? Boolean.TRUE : Boolean.FALSE;
				} else if (method.equals(m_toStringMethod)) {
					return this.toString();
				} else {
					throw new InternalError("Unexpected Object method dispatched: " + method);
				}
			}

			// InvocationEvent ie = null;
			long requestTime = System.currentTimeMillis(), responseTime;
			Object result = null;
			boolean success = true;
			try {
				synchronized (serviceReference) {
						result = method.invoke(this.getServiceObject(), args);
				}
			} catch (InvocationTargetException e) {
				Throwable rawException = e.getTargetException();
				exceptionClassName = rawException.getClass().getName();
				exceptionMessage = rawException.getMessage();
				success = false;
				// rawException.printStackTrace();
				throw rawException;

			} catch (Exception exc) {
				exceptionClassName = exc.getClass().getName();
				exceptionMessage = exc.getMessage();
				success = false;
				// exc.printStackTrace();
				throw exc;
			} finally {
				responseTime = System.currentTimeMillis();
				if (getServiceObject() != null) {

					Map<String, String> parameterTypes = new HashMap<String, String>();
					Map<String, Object> parameterValues = new HashMap<String, Object>();

					for (int i = 0; i < method.getParameterTypes().length; i++) {
						parameterTypes.put(i + "", method.getParameterTypes()[i].getName());
						parameterValues.put(i + "", args[i]);
					}
					String returnType = method.getReturnType().getName();

					notifyInvocation(consumerId, serviceId, method.getName(),
							requestTime, responseTime, success, exceptionClassName,
							exceptionMessage, parameterTypes, parameterValues, returnType,
							result);
					logger.info(serviceId+","+ System.currentTimeMillis()+"," + (responseTime - requestTime));
				}
			}
			return result;
		}

		private void notifyInvocation(String consumerId, String serviceId, String operationName,
				long requestTimestamp, long responseTimestamp, boolean success,
				String exceptionClass, String exceptionMessage, Map<String, String> parameterTypes,
				Map<String, Object> parameterValues, String returnType, Object returnValue) {

			String source = String.format("%s%s%s", serviceId, Constants.TOKEN, operationName);

			Map<String, Object> metadata = this.loadInvocationMetadata(source);
			Map<String, Object> data = this.loadInvocationData(consumerId, serviceId,
					operationName, requestTimestamp, responseTimestamp, success, exceptionClass,
					exceptionMessage, parameterTypes, parameterValues, returnType, returnValue);

			distribuitionService.postEvent(Constants.INVOCATION_EVENT, metadata, data);
			
		}

		private Map<String, Object> loadInvocationMetadata(String source) {
			Map<String, Object> metadata = new HashMap<String, Object>();
			metadata.put("source", source);

			return metadata;
		}

		private Map<String, Object> loadInvocationData(String consumerId, String serviceId,
				String operationName, long requestTimestamp, long responseTimestamp,
				boolean success, String exceptionClass, String exceptionMessage,
				Map<String, String> parameterTypes, Map<String, Object> parameterValues,
				String returnType, Object returnValue) {

			Map<String, Object> data = new HashMap<String, Object>();

			data.put(Constants.CONSUMER_ID, consumerId);
			data.put(Constants.SERVICE_ID, serviceId);
			data.put("operationName", operationName);
			data.put("requestTimestamp", requestTimestamp);
			data.put("responseTimestamp", responseTimestamp);
			data.put("success", success);

			if (exceptionClass != null) {
				data.put("exceptionMessage", exceptionMessage);
				data.put("exceptionClass", exceptionClass);
			}
			data.put("parameterTypes", parameterTypes);
			data.put("parameterValues", parameterValues);
			data.put("returnType", returnType);
			data.put("returnValue", returnValue);

			return data;
		}
	}
	
	
	
}
