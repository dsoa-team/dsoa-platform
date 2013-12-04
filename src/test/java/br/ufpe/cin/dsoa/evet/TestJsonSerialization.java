package br.ufpe.cin.dsoa.evet;

import static org.junit.Assert.*;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.epcenter.helper.HelperEpCenterTest;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;
import br.ufpe.cin.dsoa.platform.event.EventTypeCatalog;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(JUnit4TestRunner.class)
public class TestJsonSerialization {
	
	@Inject
	private BundleContext context;
	
	private EventProcessingService epService;
	private EventTypeCatalog eventTypeCatalog;
	
	
	@Before
	public void setUp() throws JAXBException {
		
		ServiceReference epCenterRef = context.getServiceReference(EventProcessingService.class
				.getName());
		if (epCenterRef != null) {
			epService = (EventProcessingService) context.getService(epCenterRef);
		}

		ServiceReference eventTypeCatalogRef = context.getServiceReference(EventTypeCatalog.class
				.getName());
		if (eventTypeCatalogRef != null) {
			eventTypeCatalog = (EventTypeCatalog) context.getService(eventTypeCatalogRef);
		}

		initializeDsoaPlatform();
		
	}
	
	private void initializeDsoaPlatform() throws JAXBException {

		Bundle[] bundles = context.getBundles();
		Bundle dsoa_bundle = null;
		for (Bundle bundle : bundles) {
			if ("dsoa-platform".equals(bundle.getSymbolicName())) {
				dsoa_bundle = bundle;
			}
		}
		HelperEpCenterTest.handleEventDefinitions(dsoa_bundle, epService, eventTypeCatalog);
		HelperEpCenterTest.handleAgentDefinitions(dsoa_bundle, epService);
	}
	
	@Configuration
	public Option[] config() {
		String configDir = "file:src/test/resources/config/";
		return options(felix(), bundle(configDir + "org.apache.felix.bundlerepository-1.6.2.jar"),
				bundle(configDir + "org.apache.felix.eventadmin-1.2.8.jar"), 
				bundle(configDir + "org.apache.felix.ipojo-1.8.0.jar"), 
				bundle(configDir + "org.apache.felix.ipojo.arch-1.6.0.jar"), 
				bundle(configDir + "org.apache.felix.ipojo.composite-1.6.0.jar"), 
				bundle(configDir + "org.apache.felix.ipojo.handler.eventadmin-1.8.0.jar"), 
				bundle(configDir + "org.apache.felix.ipojo.handler.whiteboard-1.6.0.jar"), 
				bundle(configDir + "org.apache.felix.shell-1.4.2.jar"), 
				bundle(configDir + "org.apache.felix.shell.tui-1.4.1.jar"), 
				bundle(configDir + "org.osgi.compendium-4.2.0.jar"),
				bundle(configDir + "dsoa/lib/antlr-runtime-3.1.1.jar"), 
				bundle(configDir + "dsoa/lib/cglib-nodep-2.2.jar"), 
				bundle(configDir + "dsoa/lib/commons-lang3-3.1.jar"), 
				bundle(configDir + "dsoa/lib/commons-logging-1.1.1.jar"), 
				bundle(configDir + "dsoa/lib/esper-4.7.0.jar"), 
				bundle(configDir + "dsoa/lib/monitoradmin-1.0.2.jar"),
				bundle(configDir + "dsoa/bin/dsoa-platform.jar"), 
				bundle(configDir + "dsoa/conf/configuration-bundle.jar")
		);
	}
	

	@Test
	public void testEventToJson() {
		assertFalse(eventTypeCatalog.getAll().size() <= 0);
	}

}
