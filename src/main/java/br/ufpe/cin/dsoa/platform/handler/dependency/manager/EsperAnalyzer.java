package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeNotificationListener;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.util.Constants;

public class EsperAnalyzer implements EventConsumer, Analyzer {

	private AttributeEventMapperCatalog attributeMapperCatalog;
	private AttributeNotificationListener listener;

	// Map<eventTypeName, AttributeEventMapper>
	private Map<String, AttributeEventMapper> mappers;
	
	
	private Map<String, Subscription> subscriptionMap;
	
	private EventProcessingService epService;
	
	private Map<String, AttributeConstraint> constraintMap;

	@Override
	public void start(String servicePid, List<AttributeConstraint> constraints,
			AttributeNotificationListener listener) {

		this.listener = listener;
		this.mappers = new HashMap<String, AttributeEventMapper>();
		this.subscriptionMap = new HashMap<String, Subscription>();
		this.constraintMap = new HashMap<String, AttributeConstraint>();

		for (AttributeConstraint constraint : constraints) {
			String operationName = constraint.getOperation();
			AttributableId attributableId = new AttributableId(servicePid, operationName);
			String attributeId = constraint.getAttributeId();
			AttributeEventMapper attMapper = attributeMapperCatalog.getAttributeEventMapper(attributeId);

			if (attMapper != null) {

				this.mappers.put(attMapper.getEventTypeName(), attMapper);
				this.constraintMap.put(attMapper.getEventTypeName(), constraint);

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
					filterExp = new FilterExpression(new Property(constraint.getThreashold(), propertyType),
							constraint.getExpression().getComplement());
					filterList.add(filterExp);
				}

				EventFilter filter = new EventFilter(filterList);
				String id = sourceType + Constants.TOKEN + attributeId;
				Subscription subscription = new Subscription(id, eventType, filter);
				this.subscriptionMap.put(id, subscription);
				this.epService.subscribe(this, subscription);
			}
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleEvent(Event event) {

		String eventTypeName = event.getEventType().getName();

		AttributeConstraint constraint = this.constraintMap.get(eventTypeName);
		AttributeEventMapper mapper = this.mappers.get(eventTypeName);
		AttributeValue value = mapper.convertToAttribute(event);

		this.listener.handleNotification(constraint, value);

	}

}
