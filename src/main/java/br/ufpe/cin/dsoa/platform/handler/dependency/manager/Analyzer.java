package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Expression;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.attribute.AttributeNotificationListener;
import br.ufpe.cin.dsoa.platform.monitor.MonitoringRegistration;
import br.ufpe.cin.dsoa.util.Constants;

public class Analyzer {

	private List<MonitoringRegistration> monitoringRegistrations;
	private DsoaPlatform dsoa;
	
	private FileHandler logFile;
	private Logger log;

	public Analyzer() {
		java.util.logging.Formatter f = new java.util.logging.Formatter() {

			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}
		};

		log = Logger.getLogger("AnalyzerLogger");
		try {
			logFile = new FileHandler("analyzer.log");
			logFile.setFormatter(f);
			log.addHandler(logFile);
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start(final String componentId, final String servicePid, List<AttributeConstraint> constraints,
			final AttributeNotificationListener listener) {
		
		this.monitoringRegistrations = new ArrayList<MonitoringRegistration>();

		for (final AttributeConstraint constraint : constraints) {
			String operationName = constraint.getOperation();
			AttributableId attributableId = new AttributableId(servicePid, operationName);
			String attributeId = constraint.getAttributeId();
			final AttributeEventMapper attMapper = dsoa.getAttEventMapperCatalog().getAttributeEventMapper(attributeId);

			if (attMapper != null) {

				EventType eventType = attMapper.getEventType();
				PropertyType sourceType = eventType.getMetadataPropertyType(Constants.EVENT_SOURCE);

				List<FilterExpression> filterList = new ArrayList<FilterExpression>();

				// Filter source
				FilterExpression filterExp = new FilterExpression(new Property(attributableId.getId(), sourceType),
						Expression.EQ);
				filterList.add(filterExp);

				List<AttributeEventPropertyMapper> propertyMappers = attMapper.getData();

				// Filter constraints
				for (AttributeEventPropertyMapper propertyMapper : propertyMappers) {
					String eventPropertyName = propertyMapper.getExpression()
							.replaceFirst(attMapper.getEventAlias() + ".", "").replaceFirst("data.", "");

					PropertyType propertyType = eventType.getDataPropertyType(eventPropertyName);
					filterExp = new FilterExpression(new Property(constraint.getThreashold(), propertyType), constraint
							.getExpression().getComplement());
					filterList.add(filterExp);
				}

				EventFilter filter = new EventFilter(filterList);
				Subscription subscription = new Subscription(eventType, filter);

				EventConsumer consumer = new EventConsumer() {

					@Override
					public void handleEvent(Event event) {

						AttributeValue value = attMapper.convertToAttribute(event);
						log.info(value.getAttribute().getId() + "," + value.getValue());
						listener.handleNotification(servicePid, constraint, value);
					}

					@Override
					public String getId() {
						return componentId;
					}
				};
				
				this.monitoringRegistrations.add(new MonitoringRegistration(consumer, subscription));
				this.dsoa.getEpService().subscribe(consumer, subscription, true);//TODO: parametrizar
			}
		}
	}

	

	public void stop() {
		
		if(this.monitoringRegistrations != null && !this.monitoringRegistrations.isEmpty()) {
			for(MonitoringRegistration registration : this.monitoringRegistrations) {
				this.dsoa.getEpService().unsubscribe(registration.getConsumer(), registration.getSubscription());
			}
		}
		this.monitoringRegistrations = null; 
	}

	public void setPlatform(DsoaPlatform dsoa) {
		this.dsoa = dsoa;
	}
}
