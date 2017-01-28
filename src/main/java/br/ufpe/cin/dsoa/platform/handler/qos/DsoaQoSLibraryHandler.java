package br.ufpe.cin.dsoa.platform.handler.qos;

import java.io.StringReader;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
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
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.qos.QoSLibrary;
import br.ufpe.cin.dsoa.api.qos.impl.QoSLibraryImpl;
import br.ufpe.cin.dsoa.api.service.impl.ComponentInstanceImpl;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.impl.AttributeCategoryAdapter;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentInstanceManager;
import br.ufpe.cin.dsoa.util.Constants;
import br.ufpe.cin.dsoa.util.DsoaSimpleLogger;

public class DsoaQoSLibraryHandler extends PrimitiveHandler {
	
	private static Logger logger = DsoaSimpleLogger.getDsoaLogger(DsoaQoSLibraryHandler.class
			.getCanonicalName(),DsoaQoSLibraryHandler.class
			.getCanonicalName() ,true, false);
	

	private Unmarshaller unmarshaller;
	private AttributeCatalog attributeCatalog;
	private AttributeCategoryAdapter attCatAdapter;
	
	@Override
	public void configure(Element metadata, Dictionary configuration)
			throws ConfigurationException {
		try {
			DsoaComponentInstanceManager manager = (DsoaComponentInstanceManager)this.getInstanceManager();
			ComponentInstanceImpl componentInstance = manager.getDsoaComponentInstance();
			DsoaPlatform dsoa = manager.getDsoaPlatform();
			
			this.unmarshaller = createUnmarshaller(Attribute.class);
			this.attributeCatalog = dsoa.getAttributeCatalog();
			this.attCatAdapter = new AttributeCategoryAdapter(attributeCatalog);
			
			Element[] qosTags = metadata.getElements(Constants.QOS_LIBRARY_HANDLER_TAG, Constants.QOS_LIBRARY_HANDLER_TAG_NAMESPACE);
			if (qosTags != null && qosTags.length != 0) {
				String libraryName = (String) qosTags[0].getAttribute(Constants.QOS_LIBRARY_NAME_TAG);
				QoSLibrary qosLib = new QoSLibraryImpl(libraryName);
				componentInstance.setQosLib(qosLib);
				
				Element[] attributes = qosTags[0].getElements("attributes");	
				StringReader reader = new StringReader(attributes[0].toXMLString());
				this.handleAttributeDefinitions(reader);
			}
		} catch (JAXBException e) {
			// This handler must remain valid = false
			e.printStackTrace();
		}
	}

	
	private void handleAttributeDefinitions(StringReader reader) {
		this.unmarshaller.setAdapter(AttributeCategoryAdapter.class, attCatAdapter);
		AttributeList attList;
		try {
			attList = (AttributeList) unmarshaller.unmarshal(reader);
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
			Element descInfo = new Element("Handler", "");
			descInfo.addAttribute(new org.apache.felix.ipojo.metadata.Attribute("name",Constants.QOS_LIBRARY_HANDLER_TAG_NAMESPACE +":"+ Constants.QOS_LIBRARY_HANDLER_TAG));
			Collection<Attribute> atts = attributeCatalog.getAttributes();
			for (Attribute att : atts) {
				Element attEl = new Element(att.getId(), att.getDescription());
				descInfo.addElement(attEl);
			}
	        return descInfo;
	        
	    }

	}

}
