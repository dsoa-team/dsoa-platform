package br.ufpe.cin.dsoa.platform.configurator;

import java.net.URL;
import java.util.HashMap;
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

import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.attribute.AttributeAlreadyCatalogedException;
import br.ufpe.cin.dsoa.attribute.AttributeList;
import br.ufpe.cin.dsoa.attribute.AttributePropertyType;
import br.ufpe.cin.dsoa.event.Event;
import br.ufpe.cin.dsoa.event.EventList;
import br.ufpe.cin.dsoa.event.agent.AgentAlreadyCatalogedException;
import br.ufpe.cin.dsoa.event.agent.AgentList;
import br.ufpe.cin.dsoa.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.attribute.impl.AttributeCategoryAdapter;
import br.ufpe.cin.dsoa.platform.attribute.mapper.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.event.AgentCatalog;

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

	// private EventProcessingService epService;
	private AttributeCatalog attributeCatalog;
	private AgentCatalog agentCatalog;

	private AttributeEventMapperCatalog attributeEventMapperCatalog;
	private AttributeCategoryAdapter attCatAdapter;

	private static Logger logger = Logger.getLogger(DsoaBundleListener.class.getName());

	public DsoaBundleListener(BundleContext context) {
		super(context, Bundle.ACTIVE, null);
		this.JAXBContexts = JAXBInitializer.initJAXBContexts();
	}

	@Override
	public void open() {
		this.attCatAdapter = new AttributeCategoryAdapter(attributeCatalog);
		super.open();
	}

	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		this.handleEventDefinitions();
		this.handleAgentDefinitions(bundle);
		return super.addingBundle(bundle, event);
	}

	
	
	private void handleEventDefinitions() {
		//Map<String, Object> 
		/*<event type="DsoaEvent" description="xxx">
		<header>
			<attributes>
				<attribute name="id" description="Attribute identification"
					type="java.lang.String" required="true"
					generated="br.ufpe.cin.dsoa.platform.event.impl.EventIdGenerator:nextId(*)" />
				<attribute name="timestamp" description="Timestamp" type="java.lang.Double"
					required="true" generated="br.ufpe.cin.dsoa.eventTimestampGenerator:timestamp()" />
			</attributes>
		</header>
	</event>

	<event type="InvocationEvent" extends="DsoaEvent" description="yyy">
		<payload>
			<attributes>
				<attribute name="provider" description="" type="java.lang.String"
					required="true" />
				<attribute name="service" description="" type="java.lang.String"
					required="true" />
				<attribute name="operation" description="" type="java.lang.String"
					required="true" />
				<attribute name="success" description="" type="java.lang.Boolean"
					required="true" />
				<attribute name="requestTimestamp" description=""
					type="java.lang.Long" required="true" />
				<attribute name="responseTimestamp" description=""
					type="java.lang.Long" required="true" />
			</attributes>
		</payload>
	</event>*/
		
	}

	@Override
	public void remove(Bundle bundle) {
		super.remove(bundle);
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
		super.modifiedBundle(bundle, event, object);
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
					logger.fine(String.format("Attribute Category: %s ", att.getCategory()));// LOG
					logger.fine(String.format("Attribute Name: %s ", att.getName()));// LOG

					List<AttributePropertyType> metaPropList = att.getMetadata();
					for (AttributePropertyType prop : metaPropList) {
						logger.fine(String.format("Attribute Meta-Prop id: %s ", prop.getId()));// LOG
						String type = prop.getType();
						logger.fine(String.format("Attribute Meta-Prop type: %s ", prop.getType()));// LOG
						Class<?> clazz = null;
						try {
							clazz = Class.forName(type);
						} catch (ClassNotFoundException e) {
							logger.warning("Property type could not be understood: " + type
									+ ". Removing property from Attribute " + att.getName() + "...");
							e.printStackTrace();
							metaPropList.remove(prop);
							continue;
						}
						prop.setClazz(clazz);
					}

					List<AttributePropertyType> dataPropList = att.getData();
					for (AttributePropertyType prop : dataPropList) {
						logger.fine(String.format("Attribute Data-Prop id: %s ", prop.getId()));// LOG
						String type = prop.getType();
						logger.fine(String.format("Attribute Data-Prop type: %s ", prop.getType()));// LOG
						Class<?> clazz = null;
						try {
							clazz = Class.forName(type);
						} catch (ClassNotFoundException e) {
							logger.warning("Property type could not be understood: " + type
									+ ". Removing property from Attribute " + att.getName() + "...");
							e.printStackTrace();
							metaPropList.remove(prop);
							continue;
						}
						prop.setClazz(clazz);
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
	
	public void setAttributeCatalog(AttributeCatalog attributeCatalog) {
		this.attributeCatalog = attributeCatalog;
	}

	public void setAttributeEventMapperCatalog(AttributeEventMapperCatalog attributeEventMapperCatalog) {
		this.attributeEventMapperCatalog = attributeEventMapperCatalog;
	}

	public void setAgentCatalog(AgentCatalog agentCatalog) {
		this.agentCatalog = agentCatalog;
	}
}