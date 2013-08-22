package br.ufpe.cin.dsoa.configurator.hook;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;

import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.attribute.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.attribute.AttributeCategory;
import br.ufpe.cin.dsoa.attribute.AttributeList;
import br.ufpe.cin.dsoa.attribute.AttributePropertyType;
import br.ufpe.cin.dsoa.attribute.impl.AttributeCategoryAdapter;
import br.ufpe.cin.dsoa.configurator.parser.JAXBInitializer;
import br.ufpe.cin.dsoa.configurator.parser.event.Event;
import br.ufpe.cin.dsoa.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.mapper.AttributeEventMapperAlreadyCatalogedException;
import br.ufpe.cin.dsoa.mapper.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.mapper.AttributeEventMapperList;
import br.ufpe.cin.dsoa.mapper.AttributeEventPropertyMapper;

/**
 * This class implements the Extender Pattern. It monitors bundle lifecycle events in order to 
 * discover new extensions (definitions of new Attributes, AttributeEventMappers or Events). 
 * The extensions discovered are cataloged in the platform.
 * 
 * @author fabions
 *
 */
public class DsoaBundleListener extends BundleTracker {

	private Map<String, Unmarshaller> JAXBContexts;
	
	//private EventProcessingService epService;
	private AttributeCatalog attributeCatalog;
	private AttributeEventMapperCatalog attributeEventMapperCatalog;
	
	private AttributeCategoryAdapter attCatAdapter;
	
	private static Logger logger = Logger.getLogger(DsoaBundleListener.class.getName());
	
	public DsoaBundleListener(BundleContext context) {
		super(context, Bundle.ACTIVE, null);
		this.JAXBContexts = JAXBInitializer.initJAXBContexts();
	}
	
	public void open() {
		this.attCatAdapter = new AttributeCategoryAdapter(attributeCatalog);
		super.open();
	}
	
	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		try {
			this.handleAttributeDefinitions(bundle);
			this.handleAttributeEventMapperDefinitions(bundle);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return super.addingBundle(bundle, event);
	}

	@Override
	public void remove(Bundle bundle) {
		super.remove(bundle);
	}
	
	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
		super.modifiedBundle(bundle, event, object);
	}
	
	private void handleAttributeDefinitions(Bundle bundle) throws JAXBException {
		URL url = bundle.getEntry(AttributeList.CONFIG);
		if(url != null) {
			Unmarshaller u = JAXBContexts.get(AttributeList.CONFIG);
			u.setAdapter(AttributeCategoryAdapter.class, attCatAdapter);
			AttributeList attList = (AttributeList)u.unmarshal(url);
			for(Attribute att : attList.getAttributes()) {
				logger.fine(String.format("Attribute Category: %s ", att.getCategory()));//LOG
				logger.fine(String.format("Attribute Name: %s ", att.getName()));//LOG
				
				List<AttributePropertyType> metaPropList = att.getMetadata();
				for (AttributePropertyType prop : metaPropList) {
					logger.fine(String.format("Attribute Meta-Prop id: %s ", prop.getId()));//LOG
					String type = prop.getType();
					logger.fine(String.format("Attribute Meta-Prop type: %s ", prop.getType()));//LOG
					Class<?> clazz = null;
					try {
						clazz = Class.forName(type);
					} catch (ClassNotFoundException e) {
						logger.warning("Property type could not be understood: " + type + ". Removing property from Attribute " + att.getName() +"...");
						e.printStackTrace();
						metaPropList.remove(prop);
						continue;
					}
					prop.setClazz(clazz);
				}
				
				List<AttributePropertyType> dataPropList = att.getData();
				for (AttributePropertyType prop : dataPropList) {
					logger.fine(String.format("Attribute Data-Prop id: %s ", prop.getId()));//LOG
					String type = prop.getType();
					logger.fine(String.format("Attribute Data-Prop type: %s ", prop.getType()));//LOG
					Class<?> clazz = null;
					try {
						clazz = Class.forName(type);
					} catch (ClassNotFoundException e) {
						logger.warning("Property type could not be understood: " + type + ". Removing property from Attribute " + att.getName() +"...");
						e.printStackTrace();
						metaPropList.remove(prop);
						continue;
					}
					prop.setClazz(clazz);
				}
				try {
					this.attributeCatalog.addAttribute(att);
				} catch (AttributeAlreadyCatalogedException e) {
					logger.warning(e.getMessage());
				}
			}
		}
	}
	
	private void handleAttributeEventMapperDefinitions(Bundle bundle) throws JAXBException {
		URL url = bundle.getEntry(AttributeEventMapperList.CONFIG);
		if(url != null) {
			Unmarshaller u = JAXBContexts.get(AttributeEventMapperList.CONFIG);
			AttributeEventMapperList attList = (AttributeEventMapperList)u.unmarshal(url);
			for(AttributeEventMapper mapper : attList.getAttributesEventMappers()) {
				logger.fine(String.format("Attribute Category: %s ", mapper.getCategory()));//LOG
				logger.fine(String.format("Attribute Name: %s ", mapper.getName()));//LOG
				logger.fine(String.format("Attribute Name: %s ", mapper.getEventType()));//LOG
				logger.fine(String.format("Attribute Name: %s ", mapper.getEventAlias()));//LOG
				
				List<AttributeEventPropertyMapper> metaPropList = mapper.getMetadata();
				for (AttributeEventPropertyMapper prop : metaPropList) {
					logger.fine(String.format("AttributeEventMapper Meta-Prop id: %s ", prop.getId()));//LOG
					logger.fine(String.format("AttributeEventMapper Meta-Prop type: %s ", prop.getExpression()));//LOG
				}
				
				List<AttributeEventPropertyMapper> dataPropList = mapper.getData();
				for (AttributeEventPropertyMapper prop : dataPropList) {
					logger.fine(String.format("AttributeEventMapper Meta-Prop id: %s ", prop.getId()));//LOG
					logger.fine(String.format("AttributeEventMapper Meta-Prop type: %s ", prop.getExpression()));//LOG
				}
				try {
					this.attributeEventMapperCatalog.addAttributeEventMapper(mapper);
				} catch (AttributeEventMapperAlreadyCatalogedException e) {
					logger.warning(e.getMessage());
				}
			}
		}
	}
	
/*	private void handleEventDefinition(Bundle bundle) throws JAXBException, ClassNotFoundException {
		URL url = bundle.getEntry(EventList.CONFIG);
		if(url != null) {
			EventList list = (EventList) JAXBContexts.get(EventList.CONFIG).unmarshal(url);
			
			//load eventMap
			for(Event e : list.getEvents()){
				eventMap.put(e.getType(), e);
			}
			
			for(Event e : list.getEvents()) {
				Map<String, Object> eventProperties = e.getProperties();
				if(null != e.getSuperType()){
					eventProperties.putAll(eventMap.get(e.getSuperType()).getProperties());
				}
				
				Set<String> keys = eventProperties.keySet();
				Map<String, Object> registedProperties = new HashMap<String, Object>(eventProperties);
				
				for(String key : keys){
					try {
						registedProperties.put(key, Class.forName((String) eventProperties.get(key)));
					} catch (ClassNotFoundException ex ){
						registedProperties.put(key, eventProperties.get(key));
					}
				}
				
				this.epService.defineEvent(e.getType(), registedProperties);
			}
		}
	}
	
	private void handleContextDefinition(Bundle bundle) throws JAXBException {
		URL url = bundle.getEntry(ContextModel.CONFIG);
		if(url != null) {
			ContextModel contextModel = (ContextModel) JAXBContexts.get(ContextModel.CONFIG).unmarshal(url);
			logger.fine("Context :");//LOG
			for(Context c : contextModel.getContexts()) {
				logger.fine(String.format("Context Category: %s", c.getCategory()));//LOG
				
				Map<String, String> elements = c.getElements();
				
				for(String key : elements.keySet()) {
					String e = elements.get(key);
					logger.fine(String.format("Element id: %s", e));
				}
			}
			
			for(ContextMapping cm : contextModel.getContextMappings()) {
				logger.fine(String.format("Context Mapping: %s", cm.getCategory()));
				
				Map<String, String> contextElements = cm.getContextElements();
				
				for(String key : contextElements.keySet()) {
					String ce = contextElements.get(key);
					logger.fine(String.format("Event Property: %s", ce));
				}
			}
		}
	}

*/	
	
	public void setAttributeCatalog(AttributeCatalog attributeCatalog) {
		this.attributeCatalog = attributeCatalog;
	}

	public void setAttributeEventMapperCatalog(AttributeEventMapperCatalog attributeEventMapperCatalog) {
		this.attributeEventMapperCatalog = attributeEventMapperCatalog;
	}
	
}
