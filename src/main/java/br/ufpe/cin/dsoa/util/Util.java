package br.ufpe.cin.dsoa.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.attribute.AttributableId;
import br.ufpe.cin.dsoa.api.event.EventProcessingService;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.PropertyType;

public class Util {

	private static Logger logger = Logger.getLogger(Util.class.getName());
	
	public static boolean isRemote(ServiceReference reference) {
		return reference.getProperty(Constants.REMOTE_SERVICE) != null;
	}

	public static boolean isProxy(ServiceReference reference) {
		return reference.getProperty(Constants.SERVICE_PROXY) != null;
	}

	public static String getId(ServiceReference reference) {
		Object pid = reference.getProperty(org.osgi.framework.Constants.SERVICE_PID);
		if (pid == null) {
			pid = reference.getProperty(org.osgi.framework.Constants.SERVICE_ID);
		}
		return pid.toString();
	}

	public static AttributableId getAttributableId(String serviceId, String operationName) {
		StringBuffer buf = new StringBuffer(serviceId);
		if (operationName != null) {
			buf.append(Constants.TOKEN).append(operationName);
		}
		return new AttributableId(buf.toString());
	}

	public static EventTypeList handlePlatformEventDefinitions(Bundle bundle,
			EventTypeCatalog eventTypeCatalog, EventProcessingService epService)
			throws JAXBException {
		URL url = bundle.getEntry(EventTypeList.CONFIG);
		EventTypeList eventList = null;
		
		if (url != null) {
			JAXBContext context = JAXBContext.newInstance(EventTypeList.class);
			Unmarshaller u = context.createUnmarshaller();
			eventList = (EventTypeList) u.unmarshal(url);
			List<EventType> list = eventList.getEvents();
			if (list != null && !list.isEmpty()) {
				List<EventType> subtypes = new ArrayList<EventType>();
				List<EventType> types = new ArrayList<EventType>();
				for (EventType eventType : eventList.getEvents()) {
					eventType.setPrimitive(true);//set primitive event
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
							logger.warning(e.getMessage());
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
							logger.warning(e.getMessage());
						}
					}
				}
			}
		}
		return eventList;
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
