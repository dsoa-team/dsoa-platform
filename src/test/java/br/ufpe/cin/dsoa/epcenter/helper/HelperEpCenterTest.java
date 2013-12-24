package br.ufpe.cin.dsoa.epcenter.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.attribute.AttributeList;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapperAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapperList;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.FilterExpression;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.agent.AgentList;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Expression;
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

	public static final String EVENT_DEFINITION_FILE = "src/test/resources/epcenter/configuration/event.xml";
	public static final String AGENT_DEFINITION_FILE = "src/test/resources/epcenter/configuration/agent.xml";
	public static final String ATTRIBUTE_DEFINITION_FILE = "src/test/resources/epcenter/configuration/attribute.xml";
	public static final String ATTRIBUTE_MAPPER_DEFINITION_FILE = "src/test/resources/epcenter/configuration/attribute-event-mapper.xml";

	/**
	 * load test configuration files and parse event definition
	 * 
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static EventTypeList handleEventDefinitions(String location,
			EventProcessingService epService, EventTypeCatalog eventTypeCatalog) throws JAXBException, FileNotFoundException {

		JAXBContext context = JAXBContext.newInstance(EventTypeList.class);
		Unmarshaller u = context.createUnmarshaller();
		EventTypeList eventList = (EventTypeList) u.unmarshal(new FileInputStream(location));

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
					try {
						eventTypeCatalog.add(type);
						epService.registerEventType(type);
					} catch (EventTypeAlreadyCatalogedException e) {
						e.printStackTrace();
					}
				}
			}

			if (!subtypes.isEmpty()) {
				for (EventType subtype : subtypes) {
					EventType superType = eventTypeCatalog.get(subtype.getSuperTypeName());
					if (superType != null) {
						Map<String, PropertyType> superMetaProps = superType.getMetadataMap();
						Map<String, PropertyType> subMetadataProps = subtype.getMetadataMap();
						copyProperties(superMetaProps, subMetadataProps);

						Map<String, PropertyType> superDataProps = superType.getDataMap();
						Map<String, PropertyType> subDataProps = subtype.getDataMap();
						copyProperties(superDataProps, subDataProps);
					}
					try {
						eventTypeCatalog.add(subtype);
						epService.registerEventType(subtype);
					} catch (EventTypeAlreadyCatalogedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return eventList;
	}

	/**
	 * load test configuration files and parse agent definition
	 * 
	 * @param agentDefinitionFile
	 * 
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static AgentList handleAgentDefinitions(String agentDefinitionFile,
			EventProcessingService epService) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(AgentList.class);
		Unmarshaller u = context.createUnmarshaller();

		AgentList agentList = (AgentList) u.unmarshal(new FileInputStream(agentDefinitionFile));

		// registers agents
		for (EventProcessingAgent agent : agentList.getAgents()) {
			epService.registerAgent(agent);
		}

		return agentList;

	}

	public static AgentList handleAgentDefinitions(Bundle bundle, EventProcessingService epService)
			throws JAXBException {

		URL url = bundle.getEntry(AgentList.CONFIG);
		AgentList agentList = null;

		if (url != null) {
			Unmarshaller u = createUnmarshaller(AgentList.class);
			agentList = (AgentList) u.unmarshal(url);

			// registers agents
			for (EventProcessingAgent agent : agentList.getAgents()) {
				epService.registerAgent(agent);
			}
		}

		return agentList;

	}

	/*public static AttributeEventMapperList handleAttributeEventMapperDefinitions(String mapperFile,
			EsperProcessingService epService, AttributeCatalog attributeCatalog,
			AttributeEventMapperCatalog attributeEventMapperCatalog) throws JAXBException,
			FileNotFoundException {

		JAXBContext context = JAXBContext.newInstance(AttributeEventMapperList.class);
		Unmarshaller u = context.createUnmarshaller();

		AttributeEventMapperList attList = null;
		try {
			attList = (AttributeEventMapperList) u.unmarshal(new FileInputStream(
					ATTRIBUTE_MAPPER_DEFINITION_FILE));
			for (AttributeEventMapper mapper : attList.getAttributesEventMappers()) {
				Attribute attribute = attributeCatalog.getAttribute(AttributeConstraint.format(
						mapper.getCategory(), mapper.getName()));
				mapper.setAttribute(attribute);
				EventType eventType = epService.getEventType(mapper.getEventTypeName());
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
	}*/

	public static AttributeList handleAttributeDefinitions(AttributeCatalog attributeCatalog)
			throws JAXBException, FileNotFoundException {

		AttributeCategoryAdapter attCatAdapter = new AttributeCategoryAdapter(attributeCatalog);

		JAXBContext context = JAXBContext.newInstance(AttributeList.class);
		Unmarshaller u = context.createUnmarshaller();
		u.setAdapter(AttributeCategoryAdapter.class, attCatAdapter);

		AttributeList attList = null;
		try {
			attList = (AttributeList) u.unmarshal(new FileInputStream(ATTRIBUTE_DEFINITION_FILE));
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

	public static EventTypeList handleEventDefinitions(Bundle bundle,
			EventProcessingService epService, EventTypeCatalog eventTypeCatalog) throws JAXBException {
		URL url = bundle.getEntry(EventTypeList.CONFIG);
		EventTypeList eventList = null;
		
		if (url != null) {
			Unmarshaller u = createUnmarshaller(EventTypeList.class);
			eventList = (EventTypeList) u.unmarshal(url);
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
						try {
							eventTypeCatalog.add(type);
							epService.registerEventType(type);
						} catch (EventTypeAlreadyCatalogedException e) {
							System.err.println(">>>>>>>>>>>>>>>>" + e.getMessage() + "<<<<<<<<<<<");
						}
					}
				}

				if (!subtypes.isEmpty()) {
					for (EventType subtype : subtypes) {
						EventType superType = eventTypeCatalog.get(subtype.getSuperTypeName());
						if (superType != null) {
							Map<String, PropertyType> superMetaProps = superType.getMetadataMap();
							Map<String, PropertyType> subMetadataProps = subtype.getMetadataMap();
							copyProperties(superMetaProps, subMetadataProps);

							Map<String, PropertyType> superDataProps = superType.getDataMap();
							Map<String, PropertyType> subDataProps = subtype.getDataMap();
							copyProperties(superDataProps, subDataProps);
						}
						try {
							eventTypeCatalog.add(subtype);
							epService.registerEventType(subtype);
						} catch (EventTypeAlreadyCatalogedException e) {
							System.err.println(">>>>>>>>>>>>>>>>" + e.getMessage() + "<<<<<<<<<<<");
						}
					}
				}
			}
		}
		return eventList;
	}

	/*
	 * contexts.put(EventTypeList.CONFIG,
	 * JAXBInitializer.createUnmarshaller(EventTypeList.class));
	 * contexts.put(AgentList.CONFIG,
	 * JAXBInitializer.createUnmarshaller(AgentList.class));
	 * contexts.put(AttributeList.CONFIG,
	 * createUnmarshaller(AttributeList.class));
	 * contexts.put(AttributeEventMapperList.CONFIG,
	 * createUnmarshaller(AttributeEventMapperList.class));
	 */

	private static Unmarshaller createUnmarshaller(Class<?> clazz) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(clazz);
		return context.createUnmarshaller();
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
			final EventProcessingService epService, final EventTypeCatalog eventTypeCatalog) {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1, EPStatement arg2,
					EPServiceProvider arg3) {
				for (EventBean e : arg0) {
					Object event = e.getUnderlying();

					String eventTypeName = e.getEventType().getName();
					@SuppressWarnings("unchecked")
					Event dsoaEvent = toEvent(eventTypeName, (Map<String, Object>) event,
							eventTypeCatalog);
					System.out.println(dsoaEvent);

				}
			}
		};
	}

	public static Event toEvent(String eventTypeName, Map<String, Object> event,
			EventTypeCatalog eventTypeCatalog) {

		EventType eventType = eventTypeCatalog.get(eventTypeName);
		Event dsoaEvent = eventType.createEvent(event);

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

		EventType invocationEventType = new EventType("InvocationEvent", metadata, data);

		return invocationEventType;
	}

	/**
	 * generates sample invocation event with random values
	 * 
	 * @return
	 */
	public static Event getSampleInvocationEvent() {
		String v_source = "host: " + System.getenv("HOSTNAME");

		return getSampleInvocationEvent(v_source);
	}

	/**
	 * generates sample invocation event with random values
	 * 
	 * @return
	 */
	public static Event getSampleInvocationEvent(String v_source) {

		Map<String, Property> metadataValue = new HashMap<String, Property>();
		Map<String, Property> dataValue = new HashMap<String, Property>();
		EventType invocationEventType = getInvocationEventType();

		/*
		 * // add metadata metadata.add(new PropertyType("timestamp",
		 * Long.class, true)); metadata.add(new PropertyType("id", String.class,
		 * true)); metadata.add(new PropertyType("source", String.class, true));
		 * // add data data.add(new PropertyType("consumerId", String.class,
		 * false)); data.add(new PropertyType("serviceId", String.class, true));
		 * data.add(new PropertyType("operationName", String.class, true));
		 * data.add(new PropertyType("requestTimestamp", Long.class, true));
		 * data.add(new PropertyType("responseTimestamp", Long.class, true));
		 * data.add(new PropertyType("success", Boolean.class, true));
		 * data.add(new PropertyType("exception", Exception.class, false));
		 */

		// metadata types
		PropertyType id = invocationEventType.getMetadataPropertyType("id");
		PropertyType timestamp = invocationEventType.getMetadataPropertyType("timestamp");
		PropertyType source = invocationEventType.getMetadataPropertyType("source");
		// metadata values
		String v_id = UUID.randomUUID().toString();
		Long v_timestamp = System.nanoTime();

		metadataValue.put(id.getName(), new Property(v_id, id));
		metadataValue.put(timestamp.getName(), new Property(v_timestamp, timestamp));
		metadataValue.put(source.getName(), new Property(v_source, source));

		// data types
		PropertyType consumerId = invocationEventType.getDataPropertyType("consumerId");
		PropertyType serviceId = invocationEventType.getDataPropertyType("serviceId");
		PropertyType operationName = invocationEventType.getDataPropertyType("operationName");
		PropertyType requestTimestamp = invocationEventType.getDataPropertyType("requestTimestamp");
		PropertyType responseTimestamp = invocationEventType
				.getDataPropertyType("responseTimestamp");
		PropertyType sucess = invocationEventType.getDataPropertyType("success");
		PropertyType exception = invocationEventType.getDataPropertyType("exception");
		// data values
		Random random = new Random();

		String v_consumerId = UUID.randomUUID().toString();
		String v_serviceId = UUID.randomUUID().toString();
		String v_operationName = String.format("op_%s", random.nextInt(100) + "");
		Long v_requestTimestamp = random.nextLong();
		Long v_responseTimestamp = random.nextLong();
		Boolean v_success = random.nextBoolean();

		dataValue.put(consumerId.getName(), new Property(v_consumerId, consumerId));
		dataValue.put(serviceId.getName(), new Property(v_serviceId, serviceId));
		dataValue.put(operationName.getName(), new Property(v_operationName, operationName));
		dataValue.put(requestTimestamp.getName(),
				new Property(v_requestTimestamp, requestTimestamp));
		dataValue.put(responseTimestamp.getName(), new Property(v_responseTimestamp,
				responseTimestamp));
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

		Event invocationEvent = new Event(invocationEventType, metadataValue, dataValue);

		return invocationEvent;
	}

	public static void publishSampleEvent(EsperProcessingService epService, Event event) {
		epService.publish(event);
	}

	public static void publishSampleInvocationEvent(EsperProcessingService epService) {
		EPServiceProvider epServiceProvider = epService.getEpProvider();
		Event event = getSampleInvocationEvent();

		String name = event.getEventType().getName();
		Map<String, Object> eventMap = event.toMap();

		epServiceProvider.getEPRuntime().sendEvent(eventMap, name);
	}

	public static EventFilter getEventFilter(PropertyType propertyType, Object propertyValue,
			Expression expression) {

		Property property = propertyType.createProperty(propertyValue);
		FilterExpression filterExpression = new FilterExpression(property, expression);

		List<FilterExpression> filterExpressions = new ArrayList<FilterExpression>();
		filterExpressions.add(filterExpression);

		EventFilter filter = new EventFilter(filterExpressions);

		return filter;
	}

	public static void listRuntimeServices(BundleContext context) throws InvalidSyntaxException {
		ServiceReference[] refs = (ServiceReference[]) context.getAllServiceReferences(null, null);
		for (ServiceReference ref : refs) {
			System.out
					.println("===================================================================");
			System.out.println("Properties: ");
			String[] keys = ref.getPropertyKeys();
			for (String key : keys) {
				System.out.println("Key: " + key);
				Object propValue = ref.getProperty(key);
				String propString = null;
				if (propValue instanceof boolean[]) {
					propString = Arrays.toString((boolean[]) propValue);
				} else if (propValue instanceof byte[]) {
					propString = Arrays.toString((byte[]) propValue);
				} else if (propValue instanceof int[]) {
					propString = Arrays.toString((int[]) propValue);
				} else if (propValue instanceof long[]) {
					propString = Arrays.toString((long[]) propValue);
				} else if (propValue instanceof float[]) {
					propString = Arrays.toString((float[]) propValue);
				} else if (propValue instanceof double[]) {
					propString = Arrays.toString((double[]) propValue);
				} else if (propValue instanceof Object[]) {
					propString = Arrays.toString((Object[]) propValue);
				} else {
					propString = propValue.toString();
				}
				System.out.println("Value: " + propString);
			}
			System.out.println("Class: " + context.getService(ref).getClass());
			;
			System.out.println("Object: " + context.getService(ref));
		}
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
