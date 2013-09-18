package br.ufpe.cin.dsoa.epcenter.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.attribute.AttributeList;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapperAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapperList;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.agent.AgentList;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.attribute.impl.AttributeCategoryAdapter;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.event.impl.EsperAgentBuilder;
import br.ufpe.cin.dsoa.platform.event.impl.EsperProcessingService;
import br.ufpe.cin.dsoa.platform.event.impl.Query;
import br.ufpe.cin.dsoa.platform.event.impl.QueryBuilder;
import br.ufpe.cin.dsoa.platform.event.impl.QueryDirector;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

public class HelperEpCenterTest {

	private static final String EVENT_DEFINITION_FILE = "src/test/resources/epcenter/configuration/event.xml";
	private static final String AGENT_DEFINITION_FILE = "src/test/resources/epcenter/configuration/agent.xml";
	private static final String ATTRIBUTE_DEFINITION_FILE = "src/test/resources/epcenter/configuration/attribute.xml";
	private static final String ATTRIBUTE_MAPPER_DEFINITION_FILE = "src/test/resources/epcenter/configuration/attribute-event-mapper.xml";

	/**
	 * load test configuration files and parse event definition
	 * 
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static EventTypeList handleEventDefinitions(
			EventProcessingService epService) throws JAXBException,
			FileNotFoundException {

		JAXBContext context = JAXBContext.newInstance(EventTypeList.class);
		Unmarshaller u = context.createUnmarshaller();
		EventTypeList eventList = (EventTypeList) u
				.unmarshal(new FileInputStream(EVENT_DEFINITION_FILE));

		List<EventType> list = eventList.getEvents();
		if (list != null && !list.isEmpty()) {
			List<EventType> subtypes = new ArrayList<EventType>();
			List<EventType> types = new ArrayList<EventType>();
			for (EventType eventType : eventList.getEvents()) {
				if (eventType.getSuperTypeName() != null) {
					subtypes.add(eventType);
				} else {
					types.add(eventType);
				}
			}

			if (!types.isEmpty()) {
				for (EventType type : types) {
					epService.registerEventType(type);
				}
			}

			if (!subtypes.isEmpty()) {
				for (EventType subtype : subtypes) {
					EventType superType = epService.getEventType(subtype
							.getSuperTypeName());
					if (superType != null) {
						Map<String, PropertyType> superMetaProps = superType
								.getMetadataMap();
						Map<String, PropertyType> subMetadataProps = subtype
								.getMetadataMap();
						copyProperties(superMetaProps, subMetadataProps);

						Map<String, PropertyType> superDataProps = superType
								.getDataMap();
						Map<String, PropertyType> subDataProps = subtype
								.getDataMap();
						copyProperties(superDataProps, subDataProps);
					}
					epService.registerEventType(subtype);
				}
			}
		}

		return eventList;
	}

	/**
	 * load test configuration files and parse agent definition
	 * 
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static AgentList handleAgentDefinitions(
			EventProcessingService epService) throws JAXBException,
			FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(AgentList.class);
		Unmarshaller u = context.createUnmarshaller();

		AgentList agentList = (AgentList) u.unmarshal(new FileInputStream(
				AGENT_DEFINITION_FILE));

		// registers agents
		for (EventProcessingAgent agent : agentList.getAgents()) {
			epService.registerAgent(agent);
		}

		return agentList;

	}

	public static AttributeEventMapperList handleAttributeEventMapperDefinitions(
			EsperProcessingService epService,
			AttributeCatalog attributeCatalog,
			AttributeEventMapperCatalog attributeEventMapperCatalog)
			throws JAXBException, FileNotFoundException {

		JAXBContext context = JAXBContext
				.newInstance(AttributeEventMapperList.class);
		Unmarshaller u = context.createUnmarshaller();

		AttributeEventMapperList attList = null;
		try {
			attList = (AttributeEventMapperList) u
					.unmarshal(new FileInputStream(
							ATTRIBUTE_MAPPER_DEFINITION_FILE));
			for (AttributeEventMapper mapper : attList
					.getAttributesEventMappers()) {
				Attribute attribute = attributeCatalog
						.getAttribute(AttributeConstraint.format(
								mapper.getCategory(), mapper.getName()));
				mapper.setAttribute(attribute);
				EventType eventType = epService.getEventType(mapper
						.getEventTypeName());
				mapper.setEventType(eventType);
				try {
					attributeEventMapperCatalog.addAttributeEventMapper(mapper);
				} catch (AttributeEventMapperAlreadyCatalogedException e) {
					e.printStackTrace();
				}
			}
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

		return attList;
	}

	public static AttributeList handleAttributeDefinitions(
			AttributeCatalog attributeCatalog) throws JAXBException,
			FileNotFoundException {

		AttributeCategoryAdapter attCatAdapter = new AttributeCategoryAdapter(
				attributeCatalog);

		JAXBContext context = JAXBContext.newInstance(AttributeList.class);
		Unmarshaller u = context.createUnmarshaller();
		u.setAdapter(AttributeCategoryAdapter.class, attCatAdapter);

		AttributeList attList = null;
		try {
			attList = (AttributeList) u.unmarshal(new FileInputStream(
					ATTRIBUTE_DEFINITION_FILE));
			for (Attribute att : attList.getAttributes()) {
				List<PropertyType> metaPropList = att.getMetadataList();
				if (metaPropList != null) {
					for (PropertyType propType : metaPropList) {
						String typeName = propType.getTypeName();
						Class<?> clazz = null;
						try {
							clazz = Class.forName(typeName);
							propType.setClazz(clazz);
							att.addMetadata(propType);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							metaPropList.remove(propType);
							continue;
						}
					}
				}
				List<PropertyType> dataPropList = att.getMetadataList();
				if (dataPropList != null) {
					for (PropertyType propType : dataPropList) {
						String typeName = propType.getTypeName();
						Class<?> clazz = null;
						try {
							clazz = Class.forName(typeName);
							propType.setClazz(clazz);
							att.addData(propType);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
							dataPropList.remove(propType);
							continue;
						}
					}
				}
				try {
					attributeCatalog.addAttribute(att);
				} catch (AttributeAlreadyCatalogedException e) {
					e.printStackTrace();
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return attList;
	}

	public static Query getQuery(EventProcessingAgent agent) {
		QueryBuilder builder = new EsperAgentBuilder(agent);
		QueryDirector director = new QueryDirector(builder);
		director.construct();
		Query query = director.getQuery();

		return query;
	}

	/**
	 * returns simple event listener
	 * 
	 * @return
	 */
	public static StatementAwareUpdateListener getEventListener(
			final EventProcessingService epService) {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				for (EventBean e : arg0) {
					Object event = e.getUnderlying();

					String eventTypeName = e.getEventType().getName();
					@SuppressWarnings("unchecked")
					Event dsoaEvent = toEvent(eventTypeName,
							(Map<String, Object>) event, epService);
					System.out.println(dsoaEvent);

				}
			}
		};
	}

	public static Event toEvent(String eventTypeName,
			Map<String, Object> event, EventProcessingService epService) {

		Event dsoaEvent = null;

		Map<String, Object> metadata = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		for (String key : ((Map<String, Object>) event).keySet()) {
			if (key.startsWith("data_")) {
				String newKey = key.replace("data_", "");
				data.put(newKey, event.get(key));
			} else if (key.startsWith("metadata_")) {
				String newKey = key.replace("metadata_", "");
				metadata.put(newKey, event.get(key));
			}
		}
		EventType eventType = epService.getEventType(eventTypeName);
		dsoaEvent = eventType.createEvent(metadata, data);

		return dsoaEvent;
	}

	/**
	 * generates invocation event type definition
	 * 
	 * @return
	 */
	public static EventType getInvocationEventType() {

		List<PropertyType> metadata = new ArrayList<PropertyType>();
		List<PropertyType> data = new ArrayList<PropertyType>();

		// add metadata
		metadata.add(new PropertyType("timestamp", Long.class, true));
		metadata.add(new PropertyType("id", String.class, true));
		metadata.add(new PropertyType("source", String.class, true));

		// add data
		data.add(new PropertyType("consumerId", String.class, false));
		data.add(new PropertyType("serviceId", String.class, true));
		data.add(new PropertyType("operationName", String.class, true));
		data.add(new PropertyType("requestTimestamp", Long.class, true));
		data.add(new PropertyType("responseTimestamp", Long.class, true));
		data.add(new PropertyType("success", Boolean.class, true));
		data.add(new PropertyType("exception", Exception.class, false));

		// data.add(new PropertyType("parameterTypes", Class[].class, true));
		// data.add(new PropertyType("parameterValues", Object[].class, true));
		// data.add(new PropertyType("returnType", Class.class, true));
		// data.add(new PropertyType("returnValue", Object.class, true));

		EventType invocationEventType = new EventType("InvocationEvent",
				metadata, data);

		return invocationEventType;
	}

	/**
	 * generates sample invocation event with random values
	 * 
	 * @return
	 */
	public static Event getSampleInvocationEvent() {

		Map<String, Property> metadataValue = new HashMap<String, Property>();
		Map<String, Property> dataValue = new HashMap<String, Property>();
		EventType invocationEventType = getInvocationEventType();
		
	/*
	 * // add metadata
		metadata.add(new PropertyType("timestamp", Long.class, true));
		metadata.add(new PropertyType("id", String.class, true));
		metadata.add(new PropertyType("source", String.class, true));

		// add data
		data.add(new PropertyType("consumerId", String.class, false));
		data.add(new PropertyType("serviceId", String.class, true));
		data.add(new PropertyType("operationName", String.class, true));
		data.add(new PropertyType("requestTimestamp", Long.class, true));
		data.add(new PropertyType("responseTimestamp", Long.class, true));
		data.add(new PropertyType("success", Boolean.class, true));
		data.add(new PropertyType("exception", Exception.class, false));
	 * */
		              
		// metadata types
		PropertyType id = invocationEventType.getMetadataPropertyType("id");
		PropertyType timestamp = invocationEventType
				.getMetadataPropertyType("timestamp");
		PropertyType source = invocationEventType
				.getMetadataPropertyType("source");
		// metadata values
		String v_id = UUID.randomUUID().toString();
		Long v_timestamp = System.nanoTime();
		String v_source = "host: " + System.getenv("hostname");

		metadataValue.put(id.getName(), new Property(v_id, id));
		metadataValue.put(timestamp.getName(), new Property(v_timestamp,
				timestamp));
		metadataValue.put(source.getName(), new Property(v_source, source));

		// data types
		PropertyType consumerId = invocationEventType
				.getDataPropertyType("consumerId");
		PropertyType serviceId = invocationEventType
				.getDataPropertyType("serviceId");
		PropertyType operationName = invocationEventType
				.getDataPropertyType("operationName");
		PropertyType requestTimestamp = invocationEventType
				.getDataPropertyType("requestTimestamp");
		PropertyType responseTimestamp = invocationEventType
				.getDataPropertyType("responseTimestamp");
		PropertyType sucess = invocationEventType
				.getDataPropertyType("success");
		PropertyType exception = invocationEventType
				.getDataPropertyType("exception");
		// data values
		Random random = new Random();

		String v_consumerId = UUID.randomUUID().toString();
		String v_serviceId = UUID.randomUUID().toString();
		String v_operationName = String.format("op_%s", random.nextInt(100)
				+ "");
		Long v_requestTimestamp = random.nextLong();
		Long v_responseTimestamp = random.nextLong();
		Boolean v_success = random.nextBoolean();

		dataValue.put(consumerId.getName(), new Property(v_consumerId,
				consumerId));
		dataValue
				.put(serviceId.getName(), new Property(v_serviceId, serviceId));
		dataValue.put(operationName.getName(), new Property(v_operationName,
				operationName));
		dataValue.put(requestTimestamp.getName(), new Property(
				v_requestTimestamp, requestTimestamp));
		dataValue.put(responseTimestamp.getName(), new Property(
				v_responseTimestamp, responseTimestamp));
		dataValue.put(sucess.getName(), new Property(v_success, sucess));
		dataValue.put(exception.getName(), new Property(null, exception));

		// dataValue.put(invocationEventType.getDataPropertyType("parameterTypes").getName(),new
		// Property("parameterTypes",
		// invocationEventType.getDataPropertyType("parameterTypes")));
		// dataValue.put(invocationEventType.getDataPropertyType("parameterValues").getName(),new
		// Property("parameterValues",
		// invocationEventType.getDataPropertyType("parameterValues")));
		// dataValue.put(invocationEventType.getDataPropertyType("returnType").getName(),new
		// Property(String.class,
		// invocationEventType.getDataPropertyType("returnType")));
		// dataValue.put(invocationEventType.getDataPropertyType("returnValue").getName(),new
		// Property("return",
		// invocationEventType.getDataPropertyType("returnValue")));

		Event invocationEvent = new Event(invocationEventType, metadataValue,
				dataValue);

		return invocationEvent;
	}

	public static void publishSampleEvent(EsperProcessingService epService,
			Event event) {
		epService.publish(event);
	}

	public static void publishSampleInvocationEvent(
			EsperProcessingService epService) {
		EPServiceProvider epServiceProvider = epService.getEpProvider();
		Event event = getSampleInvocationEvent();

		String name = event.getEventType().getName();
		Map<String, Object> eventMap = event.toMap();

		epServiceProvider.getEPRuntime().sendEvent(eventMap, name);
	}

	private static void copyProperties(Map<String, PropertyType> superProps,
			Map<String, PropertyType> subProps) {
		if (!superProps.isEmpty()) {
			Set<String> keys = superProps.keySet();
			for (String key : keys) {
				subProps.put(key, superProps.get(key));
			}
		}
	}

}
