package br.ufpe.cin.dsoa.platform.handler.qos;

import java.io.StringReader;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.api.attribute.AttributeList;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapperList;
import br.ufpe.cin.dsoa.api.event.EventTypeList;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.agent.AgentList;
import br.ufpe.cin.dsoa.api.qos.QoSLibrary;
import br.ufpe.cin.dsoa.api.qos.impl.QoSLibraryImpl;
import br.ufpe.cin.dsoa.api.service.ComponentInstance;
import br.ufpe.cin.dsoa.api.service.impl.ComponentInstanceImpl;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.impl.AttributeCategoryAdapter;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentInstanceManager;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.DsoaSimpleLogger;

public class DsoaQoSLibraryHandler extends PrimitiveHandler {
	
	private static final int ADDED = 1;
	
	private static Logger logger = DsoaSimpleLogger.getDsoaLogger(DsoaQoSLibraryHandler.class
			.getCanonicalName(),DsoaQoSLibraryHandler.class
			.getCanonicalName() ,true, false);
	

	private Map<String, Unmarshaller> contexts;
	private AttributeCatalog attributeCatalog;
	private AttributeCategoryAdapter attCatAdapter;

	@Override
	public void configure(Element metadata, Dictionary configuration)
			throws ConfigurationException {
		DsoaComponentInstanceManager manager = (DsoaComponentInstanceManager)this.getInstanceManager();
		ComponentInstanceImpl componentInstance = manager.getDsoaComponentInstance();
		DsoaPlatform dsoa = manager.getDsoaPlatform();
		this.attributeCatalog = dsoa.getAttributeCatalog();
		this.attCatAdapter = new AttributeCategoryAdapter(attributeCatalog);
		this.contexts = initJAXBContexts();
		
		Element[] qosTags = metadata.getElements(Constants.QOS_LIBRARY_HANDLER_TAG, Constants.QOS_LIBRARY_HANDLER_TAG_NAMESPACE);
		if (qosTags != null && qosTags.length != 0) {
			String libraryName = (String) qosTags[0].getAttribute(Constants.QOS_LIBRARY_NAME_TAG);
			QoSLibrary qosLib = new QoSLibraryImpl(libraryName);
			componentInstance.setQosLib(qosLib);
			
			Element[] attributes = qosTags[0].getElements("attributes");	
			StringReader reader = new StringReader(attributes[0].toXMLString());
			this.handleAttributeDefinitions(reader, ADDED);
			
			
			/*Element[] categoryTags = qosTags[0].getElements(Constants.CATEGORY_TAG);			
			for (Element categoryTag : categoryTags) {
				String categoryName = (String) categoryTag.getAttribute(Constants.CATEGORY_NAME_TAG);
				Category category = new CategoryImpl(categoryName);
				
				Element[] attTags = categoryTag.getElements(Constants.ATTRIBUTE_TAG);
				for (Element attTag : attTags) {
					String attName = (String) attTag.getAttribute(Constants.ATTRIBUTE_NAME_TAG);
					Attribute attribute = new AttributeImpl(category, attName);
					
					Element[] metricTags = attTag.getElements(Constants.METRIC_TAG);
					for (Element metricTag : metricTags) {
						String metricName = (String) metricTag.getAttribute(Constants.METRIC_NAME_TAG);
						Metric metric = new MetricImpl(attribute, metricName);
						qosLib.addMetric(metric);
					}
				}
			}*/
		}

	}

	
	private void handleAttributeDefinitions(StringReader reader, int action) {
		Unmarshaller u = contexts.get("Attributes");
		u.setAdapter(AttributeCategoryAdapter.class, attCatAdapter);
		AttributeList attList;
		try {
			attList = (AttributeList) u.unmarshal(reader);
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
							logger.warning("Property type could not be understood: "
									+ typeName
									+ ". Removing property from Attribute "
									+ att.getName() + "...");
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
							logger.warning("Property type could not be understood: "
									+ typeName
									+ ". Removing property from Attribute "
									+ att.getName() + "...");
							e.printStackTrace();
							dataPropList.remove(propType);
							continue;
						}
					}
				}
				try {
					if (!this.attributeCatalog.containsAttribute(att.getId())) {
						attributeCatalog.addAttribute(att);
					}
				} catch (AttributeAlreadyCatalogedException e) {
					logger.warning(e.getMessage());
				}
			}
		} catch (JAXBException e) {
			logger.warning("There was an error. Corresponding attribute definitions will not be considered!");
		}
	}
	
	
	public Map<String, Unmarshaller> initJAXBContexts() {
		Map<String, Unmarshaller> contexts = new HashMap<String, Unmarshaller>();
		try {
			contexts.put("Events", createUnmarshaller(EventTypeList.class));
			contexts.put("Agents", createUnmarshaller(AgentList.class));
			contexts.put("Attributes", createUnmarshaller(AttributeList.class));
			contexts.put("Mappers", createUnmarshaller(AttributeEventMapperList.class));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return contexts;
	}
	
	
	private Unmarshaller createUnmarshaller(Class<?> clazz) throws JAXBException {
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
		
		return new DsoaQoSLibraryHandlerDescription(this);
	}
	
	class DsoaQoSLibraryHandlerDescription extends HandlerDescription {

		public DsoaQoSLibraryHandlerDescription(DsoaQoSLibraryHandler autonomicHandler) {
			super(autonomicHandler);
		}
		
		public Element getHandlerInfo() {
			DsoaComponentInstanceManager manager = (DsoaComponentInstanceManager)getInstanceManager();
			ComponentInstance componentInstance = manager.getDsoaComponentInstance();
			Element descInfo = new Element("Handler", "");
			descInfo.addAttribute(new org.apache.felix.ipojo.metadata.Attribute("name",Constants.QOS_LIBRARY_HANDLER_TAG_NAMESPACE +":"+ Constants.QOS_LIBRARY_HANDLER_TAG));
			/*if (componentInstance.getQosLib() != null) {
		        for (Metric metric : componentInstance.getQosLib().getMetrics()) {
		            descInfo.addElement(new Element("Metric",metric.getFullname()));
		        }
			}*/
	        return descInfo;
	        
	    }

	}

}
