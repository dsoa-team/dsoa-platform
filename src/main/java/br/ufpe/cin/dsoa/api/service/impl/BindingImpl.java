package br.ufpe.cin.dsoa.api.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.felix.ipojo.FieldInterceptor;

import br.ufpe.cin.dsoa.api.attribute.AttributableId;
import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventPropertyMapper;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.FilterExpression;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.service.Binding;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.DsoaComponentInstance;
import br.ufpe.cin.dsoa.api.service.Port;
import br.ufpe.cin.dsoa.api.service.RelationalOperator;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.handler.requires.DsoaBindingManager;
import br.ufpe.cin.dsoa.platform.handler.requires.DsoaRequiresHandler;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringRegistration;
import br.ufpe.cin.dsoa.util.Constants;

/**
 * Represents an Binding implementation
 * 
 * @author fabions
 * 
 */
public class BindingImpl extends PortInstanceImpl implements Binding,
		FieldInterceptor {

	/**
	 * A meta-object that represents the component instance which owns this
	 * binding
	 */
	private DsoaComponentInstance componentInstance;

	/**
	 * A meta-model object that represents the bound service instance
	 */
	private ServiceInstance serviceInstance;

	/**
	 * A proxy to the real service instance, which is responsible for
	 * intercepting invocations to the required service in order to monitor QoS.
	 */
	private Object proxy;

	/**
	 * A reference to the autonomic manager, which is responsible for managing
	 * this binding
	 */
	private DsoaBindingManager manager;

	/**
	 * Points to the handler responsible for managing this dependency
	 */
	private DsoaRequiresHandler handler;

	/**
	 * The binding's status
	 */
	private boolean valid;
	
	private List<MonitoringRegistration> monitoringRegistrations;

	private Logger logger;

	public BindingImpl(DsoaRequiresHandler handler,
			DsoaComponentInstance componentInstance, Port port,
			List<br.ufpe.cin.dsoa.api.service.Property> props) {
		// Sets the name of this binding to the following format:
		// <component-instance-name>-<required-port-name>
		super(componentInstance.getName() + "-" + port.getName(), port, props);

		this.handler = handler;

		this.componentInstance = componentInstance;

		// Add this Binding to the ComponentInstance to keep consistency
		this.componentInstance.addBinding(this);

		// Instantiate the Autonomic Binding Manager
		this.manager = new DsoaBindingManager(this);

		// TODO Verify where exactly the class is required
		/*
		 * Class<?> clazz = null; try { clazz =
		 * getInstanceManager().getClazz().getClassLoader
		 * ().loadClass(itfClassname);
		 */
		createLogger();
	}

	public void createLogger() {
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
			FileHandler adaptationLogFile = new FileHandler("Binding.log");
			adaptationLogFile.setFormatter(f);
			logger.addHandler(adaptationLogFile);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DsoaPlatform getDsoaPlatform() {
		return ((DsoaComponentInstanceImpl) this.componentInstance)
				.getInstanceManager().getDsoaPlatform();
	}

	/**
	 * Represents the bind action (available to the executor portion of the
	 * BindingManager
	 */
	public void bind(ServiceInstance serviceInstance) {
		if (!isBound()) {
			synchronized(this) {
				this.serviceInstance = serviceInstance;
				this.proxy = this.getDsoaPlatform().getProxyFactory()
					.getProxy(componentInstance.getName(), serviceInstance);
				this.setValid(true);
			}
			this.startMonitoring();
			this.notifyBind();
		} else {
			logger.log(Level.WARNING, "Binding " + this.getName()
					+ " is already bound");
		}
	}

	/**
	 * Represents the unbind action (available to the executor portion of the
	 * BindingManager
	 */
	public void unbind() {
		if (isBound()) {
			synchronized(this) {
				((ServiceInstanceProxyItf)this.serviceInstance).ungetServiceObject();
				this.proxy = null;
				this.setValid(false);
			}
			this.notifyUnbind();
		} else {
			logger.log(Level.WARNING, "Binding " + this.getName()
					+ " is already unbound");
		}
	}

	public boolean isBound() {
		return valid && (serviceInstance != null);
	}

	public void onSet(Object pojo, String fieldName, Object value) {
		// Just do nothing...
	}

	public Object onGet(Object pojo, String fieldName, Object value) {
		return this.proxy;
	}

	public ServiceInstance getServiceInstance() {
		return this.serviceInstance;
	}

	public DsoaComponentInstance getComponentInstance() {
		return this.componentInstance;
	}

	@Override
	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
		if (valid) {
			handler.computeState();
		} else {
			handler.setValidity(false);
		}
	}

	public void start() {
		manager.start();
	}

	public void stop() {
		this.unbind();
		manager.stop();
	}

	private void startMonitoring() {

		this.monitoringRegistrations = new ArrayList<MonitoringRegistration>();

		for (final Constraint constraint : this.getPort()
				.getServiceSpecification().getNonFunctionalSpecification()
				.getConstraints()) {
			String operationName = constraint.getOperation();
			AttributableId attributableId = new AttributableId(this.getServiceInstance().getName(), operationName);
			String attributeId = constraint.getAttributeId();
			final AttributeEventMapper attMapper = this.getDsoaPlatform().getAttEventMapperCatalog()
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
				this.getDsoaPlatform().getEpService()
						.subscribe(consumer, subscription, true);// TODO:
																	// parametrizar
			}
		}
	}
	
	public void stopMonitoring() {
		if(this.monitoringRegistrations != null && !this.monitoringRegistrations.isEmpty()) {
			for(MonitoringRegistration registration : this.monitoringRegistrations) {
				this.getDsoaPlatform().getEpService().unsubscribe(registration.getConsumer(), registration.getSubscription());
			}
		}
		this.monitoringRegistrations = null; 
	}

	private void notify(String eventTypeName) {
		// TODO Assert that the instance is always not null whenever this method
		// is called.
		String serviceId = getServiceInstance().getName();
		String consumerId = getComponentInstance().getName();
		String serviceInterface = getPort().getServiceSpecification()
				.getFunctionalInterface().getInterfaceName();

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Constants.SERVICE_ID, serviceId);
		data.put(Constants.CONSUMER_ID, consumerId);
		data.put(Constants.SERVICE_INTERFACE, serviceInterface);

		Map<String, Object> metadata = new HashMap<String, Object>();
		// TODO BY NOW, THE Binding name is set to required field name
		metadata.put(Constants.EVENT_SOURCE,
				String.format("%s.%s", componentInstance.getName(), getName()));

		getDsoaPlatform().getEventDistribuitionService().postEvent(
				eventTypeName, metadata, data);
		logger.info("Binding notification: " + eventTypeName + " at "
				+ System.currentTimeMillis() + "," + consumerId + ":"
				+ serviceId);
	}

	private void notifyUnbind() {
		this.notify(Constants.UNBIND_EVENT);
	}

	private void notifyBind() {
		this.notify(Constants.BIND_EVENT);
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
			manager.constraintViolated(getServiceInstance().getName(), constraint, value);
		}

		@Override
		public String getId() {
			return getComponentInstance().getName();
		}
	}
}
