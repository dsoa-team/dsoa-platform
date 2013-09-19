package br.ufpe.cin.dsoa.epcenter.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.Property;
import br.ufpe.cin.dsoa.api.event.PropertyType;

public class EventProducerMock {

	private EventType eventType;
	private Random random;

	public EventProducerMock(EventType eventType) {
		this.eventType = eventType;
		this.random = new Random();
	}

	public Event getEvent(String service, String operation, long requestTime, long responseTime) {

		Map<String, Property> metadata = new HashMap<String, Property>();
		Map<String, Property> data = new HashMap<String, Property>();

		String source = (operation == null) ? service : String.format("%s.%s", service, operation);

		// metadata
		for (PropertyType propertyType : eventType.getMetadataList()) {
			Object value = (propertyType.getFullname().equals("metadata_source")) ? source : genRandom(propertyType
					.getType());
			Property property = propertyType.createProperty(value);
			metadata.put(propertyType.getName(), property);
		}

		// data
		for (PropertyType propertyType : eventType.getDataList()) {
			Object value = null;

			if (propertyType.getFullname().equals("data_requestTimestamp")) {
				value = requestTime;
			} else if (propertyType.getFullname().equals("data_responseTimestamp")) {
				value = responseTime;
			} else {
				value = genRandom(propertyType.getType());
			}
			Property property = propertyType.createProperty(value);
			data.put(propertyType.getName(), property);
		}

		Event generetedEvent = new Event(eventType, metadata, data);
		
		return generetedEvent;
	}

	public Event getEvent(String service, String operation) {
		return getEvent(service, operation, (Long) genRandom(Long.class), 
				(Long) genRandom(Long.class));
	}

	private Object genRandom(Class<?> clazz) {

		Object rand = null;

		if (clazz == Boolean.class) {
			rand = random.nextBoolean();
		} else if (clazz == Integer.class) {
			rand = random.nextInt();
		} else if (clazz == Long.class) {
			rand = random.nextLong();
		} else if (clazz == Double.class) {
			rand = random.nextDouble();
		} else if (clazz == String.class) {
			rand = UUID.randomUUID().toString();
		} else {
			try {
				rand = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return rand;
	}
}
