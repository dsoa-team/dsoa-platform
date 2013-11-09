package br.ufpe.cin.dsoa.platform.configurator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.attribute.AttributeList;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapperAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapperList;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.agent.AgentAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.event.agent.AgentList;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.api.event.agent.ProcessingMapping;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.attribute.impl.AttributeCategoryAdapter;
import br.ufpe.cin.dsoa.platform.event.AgentCatalog;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.platform.resource.ResourceManager;

/**
 * This class implements the Extender Pattern. It monitors bundle lifecycle
 * events in order to discover new extensions (definitions of new Attributes,
 * AttributeEventMappers or Events). The extensions discovered are cataloged in
 * the platform.
 * 
 * @author fabions
 * 
 */
public class DsoaBundleListener extends BundleTracker {

	private Map<String, Unmarshaller> JAXBContexts;

	private EventProcessingService epService;
	private ResourceManager resourceManager;
	
	private AttributeCatalog attributeCatalog;
	private AgentCatalog agentCatalog;
	private EventTypeCatalog eventTypeCatalog;

	private AttributeEventMapperCatalog attributeEventMapperCatalog;
	private AttributeCategoryAdapter attCatAdapter;

	private static Logger logger = Logger.getLogger(DsoaBundleListener.class.getName());

	public DsoaBundleListener(BundleContext context) {
		super(context, Bundle.INSTALLED, null);
		this.JAXBContexts = JAXBInitializer.initJAXBContexts();
	}

	@Override
	public void open() {
		this.attCatAdapter = new AttributeCategoryAdapter(attributeCatalog);
		super.open();
	}

	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		this.handleEventDefinitions(bundle);
		this.handleAttributeDefinitions(bundle);
		this.handleAgentDefinitions(bundle);
		this.handleAttributeEventMapperDefinitions(bundle);

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
	
	private void handleEventDefinitions(Bundle bundle) {
		URL url = bundle.getEntry(EventTypeList.CONFIG);
		if (url != null) {
			Unmarshaller u = JAXBContexts.get(EventTypeList.CONFIG);
			EventTypeList eventList = null;
			try {
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
								this.eventTypeCatalog.add(type);
								this.epService.registerEventType(type);
							} catch (EventTypeAlreadyCatalogedException e) {
								logger.warning(e.getMessage());
							}
						}
					}
					
					if (!subtypes.isEmpty()) {
						for (EventType subtype : subtypes) {
							EventType superType = this.eventTypeCatalog.get(subtype.getSuperTypeName());
							if (superType != null) {
								Map<String, PropertyType> superMetaProps = superType.getMetadataMap();
								Map<String, PropertyType> subMetadataProps = subtype.getMetadataMap();
								copyProperties(superMetaProps, subMetadataProps);
								
								Map<String, PropertyType> superDataProps = superType.getDataMap();
								Map<String, PropertyType> subDataProps = subtype.getDataMap();
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
				
			} catch (JAXBException e1) {
				logger.warning("There was an error while processing file " + url
						+ ". Corresponding mapper definitions will not be considered!");
				e1.printStackTrace();
			}
		}
	}

	private void copyProperties(Map<String, PropertyType> superProps, Map<String, PropertyType> subProps) {
		if (!superProps.isEmpty()) {
			Set<String> keys = superProps.keySet();
			for (String key : keys) {
				subProps.put(key, superProps.get(key));
			}
		}
	}
	
	private void handleAttributeEventMapperDefinitions(Bundle bundle) {
		URL url = bundle.getEntry(AttributeEventMapperList.CONFIG);
		if (url != null) {
			Unmarshaller u = JAXBContexts.get(AttributeEventMapperList.CONFIG);
			AttributeEventMapperList attList = null;
			try {
				attList = (AttributeEventMapperList) u.unmarshal(url);
				for (AttributeEventMapper mapper : attList.getAttributesEventMappers()) {
					Attribute attribute = attributeCatalog.getAttribute(mapper.getCategory(), mapper.getName());
					mapper.setAttribute(attribute);
					EventType eventType = eventTypeCatalog.get(mapper.getEventTypeName());
					mapper.setEventType(eventType);
					try {
						this.attributeEventMapperCatalog.addAttributeEventMapper(mapper);
					} catch (AttributeEventMapperAlreadyCatalogedException e) {
						logger.warning(e.getMessage());
					}
				}
			} catch (JAXBException e1) {
				logger.warning("There was an error while processing file " + url
						+ ". Corresponding mapper definitions will not be considered!");
				e1.printStackTrace();
			}
		}
	}

	private void handleAgentDefinitions(Bundle bundle) {
		URL url = bundle.getEntry(AgentList.CONFIG);
		if (url != null) {
			Unmarshaller u = JAXBContexts.get(AgentList.CONFIG);
			AgentList agentList;
			try {
				agentList = (AgentList) u.unmarshal(url);

				for (EventProcessingAgent eventProcessingAgent : agentList.getAgents()) {
					try {
						this.agentCatalog.addAgent(eventProcessingAgent);
						this.epService.registerAgent(eventProcessingAgent);
						
						if(eventProcessingAgent.getProcessing() instanceof ProcessingMapping){
							this.resourceManager.manage(eventProcessingAgent);
						}
						
					} catch (AgentAlreadyCatalogedException e) {
						logger.warning(e.getMessage());
					}
				}
			} catch (JAXBException e) {
				logger.warning("There was an error while processing file " + url
						+ ". Corresponding agent definitions will not be considered!");
				e.printStackTrace();
			}
		}
	}

	private void handleAttributeDefinitions(Bundle bundle) {
		URL url = bundle.getEntry(AttributeList.CONFIG);
		if (url != null) {
			Unmarshaller u =  JAXBContexts.get(AttributeList.CONFIG);
			u.setAdapter(AttributeCategoryAdapter.class, attCatAdapter);
			AttributeList attList;
			try {
				attList = (AttributeList) u.unmarshal(url);
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
								logger.warning("Property type could not be understood: " + typeName
										+ ". Removing property from Attribute " + att.getName() + "...");
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
								logger.warning("Property type could not be understood: " + typeName
										+ ". Removing property from Attribute " + att.getName() + "...");
								e.printStackTrace();
								dataPropList.remove(propType);
								continue;
							}
						}
					}
					try {
						attributeCatalog.addAttribute(att);
					} catch (AttributeAlreadyCatalogedException e) {
						logger.warning(e.getMessage());
					}
				}
			} catch (JAXBException e) {
				logger.warning("There was an error while processing file " + url
						+ ". Corresponding attribute definitions will not be considered!");
			}
		}
	}

	public void setEventProcessingService(EventProcessingService epService) {
		this.epService = epService;
	}
	
	public void setAttributeCatalog(AttributeCatalog attributeCatalog) {
		this.attributeCatalog = attributeCatalog;
	}

	public void setAttributeEventMapperCatalog(AttributeEventMapperCatalog attributeEventMapperCatalog) {
		this.attributeEventMapperCatalog = attributeEventMapperCatalog;
	}

	public void setAgentCatalog(AgentCatalog agentCatalog) {
		this.agentCatalog = agentCatalog;
	}

	public void setEventTypeCatalog(EventTypeCatalog eventTypeCatalog) {
		this.eventTypeCatalog = eventTypeCatalog;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		
	}
}
