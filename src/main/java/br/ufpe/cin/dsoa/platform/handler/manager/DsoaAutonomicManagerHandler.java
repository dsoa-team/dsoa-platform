package br.ufpe.cin.dsoa.platform.handler.manager;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.api.event.EventProcessingService;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentInstanceManager;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.DsoaSimpleLogger;

public class DsoaAutonomicManagerHandler extends PrimitiveHandler {

	private static Logger logger = DsoaSimpleLogger.getDsoaLogger(
			DsoaAutonomicManagerHandler.class.getCanonicalName(),
			DsoaAutonomicManagerHandler.class.getCanonicalName(), true, false);

	private Unmarshaller unmarshaller;

	private EventTypeCatalog eventTypeCatalog;

	private EventProcessingService epService;

	@Override
	@SuppressWarnings("rawtypes")
	public void configure(Element metadata, Dictionary configuration)
			throws ConfigurationException {
		try {
			DsoaComponentInstanceManager manager = (DsoaComponentInstanceManager) this
					.getInstanceManager();
			DsoaPlatform dsoa = manager.getDsoaPlatform();
			this.epService = dsoa.getEpService();

			this.unmarshaller = createUnmarshaller(EventTypeList.class);
			this.eventTypeCatalog = dsoa.getEventTypeCatalog();

			Element[] managerTag = metadata.getElements(
					Constants.AUTONOMIC_HANDLER_TAG,
					Constants.AUTONOMIC_HANDLER_TAG_NAMESPACE);

			if (managerTag != null && managerTag.length != 0) {
				Element[] eventLibTag = managerTag[0].getElements(
						Constants.EVENT_LIBRARY_TAG, null);
				System.out.println(eventLibTag[0].toXMLString());
				if (eventLibTag != null && eventLibTag.length != 0) {
					StringReader reader = new StringReader(
							eventLibTag[0].toXMLString());
					this.handleEventDefinitions(reader);
				}
			}
		} catch (JAXBException e) {
			// This handler must remain valid = false
			e.printStackTrace();
		}

	}

	private void handleEventDefinitions(StringReader reader) {
		EventTypeList eventList = null;
		try {
			eventList = (EventTypeList) this.unmarshaller.unmarshal(reader);
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

						if (!this.eventTypeCatalog.contains(type.getName())) {
							try {
								this.eventTypeCatalog.add(type);
								this.epService.registerEventType(type);
							} catch (EventTypeAlreadyCatalogedException e) {
								logger.warning(e.getMessage());
							}
						}

					}
				}

				if (!subtypes.isEmpty()) {
					for (EventType subtype : subtypes) {
						if (!this.eventTypeCatalog.contains(subtype.getName())) {
							EventType superType = this.eventTypeCatalog
									.get(subtype.getSuperTypeName());
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
							try {
								this.eventTypeCatalog.add(subtype);
								this.epService.registerEventType(subtype);
							} catch (EventTypeAlreadyCatalogedException e) {
								logger.warning(e.getMessage());
							}
						}
					}
				}
			}

		} catch (JAXBException e1) {
			logger.warning("There was an error while processing file. Corresponding mapper definitions will not be considered!");
			e1.printStackTrace();
		}
	}
	

	private void copyProperties(Map<String, PropertyType> superProps,
			Map<String, PropertyType> subProps) {
		if (!superProps.isEmpty()) {
			Set<String> keys = superProps.keySet();
			for (String key : keys) {
				subProps.put(key, superProps.get(key));
			}
		}
	}

	private Unmarshaller createUnmarshaller(Class<?> clazz)
			throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(clazz);
		return context.createUnmarshaller();
	}

	@Override
	public void stop() {
		this.setValidity(false);
	}

	@Override
	public void start() {
		this.setValidity(true);
	}

	@Override
	public HandlerDescription getDescription() {

		return new DsoaAutonomicHandlerDescription(this);
	}

	class DsoaAutonomicHandlerDescription extends HandlerDescription {

		public DsoaAutonomicHandlerDescription(
				DsoaAutonomicManagerHandler autonomicHandler) {
			super(autonomicHandler);
		}

		public Element getHandlerInfo() {
			
			Element descInfo = new Element("Handler", "");
			descInfo.addAttribute(new org.apache.felix.ipojo.metadata.Attribute(
					"name", Constants.AUTONOMIC_HANDLER_TAG_NAMESPACE + ":"
							+ Constants.AUTONOMIC_HANDLER_TAG));
			for(EventType eventType : eventTypeCatalog.getAll()) {
				Element evType = new Element(eventType.getName(), eventType.getSuperTypeName());
				descInfo.addElement(evType);
				
				
				List<PropertyType> mTypes = eventType.getMetadataList();
				if (mTypes != null && !mTypes.isEmpty()) {
					Element mPro = new Element("Meta Properties:", null);
					evType.addElement(mPro);
					for(PropertyType propType : mTypes)	{
						Element p = new Element(propType.getName(),propType.getTypeName());
						mPro.addElement(p);
					}
				}
				
				
				List<PropertyType> propTypes = eventType.getDataList();
				if (propTypes != null && !propTypes.isEmpty()) {
					Element dataPro = new Element("Data Properties:", null);
					evType.addElement(dataPro);
					for(PropertyType propType : propTypes)	{
						Element p = new Element(propType.getName(),propType.getTypeName());
						dataPro.addElement(p);
					}
				}

			}
			return descInfo;

		}

	}

}
