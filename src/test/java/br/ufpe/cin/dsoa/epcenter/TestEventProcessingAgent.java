package br.ufpe.cin.dsoa.epcenter;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.TimeoutOption;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.service.Expression;
import br.ufpe.cin.dsoa.epcenter.helper.EventProducerMock;
import br.ufpe.cin.dsoa.epcenter.helper.HelperEpCenterTest;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;

@RunWith(JUnit4TestRunner.class)
public class TestEventProcessingAgent {

	@Inject
	private BundleContext context;

	private EventProcessingService epCenter;
	private EventProducerMock mock;

	private EventTypeCatalog eventTypeCatalog;

	@Before
	public void setup() throws JAXBException {
		ServiceReference epCenterRef = context
				.getServiceReference(br.ufpe.cin.dsoa.platform.event.EventProcessingService.class
						.getName());
		if (epCenterRef != null) {
			epCenter = (EventProcessingService) context.getService(epCenterRef);
		}

		ServiceReference eventTypeCatalogRef = context.getServiceReference(EventTypeCatalog.class
				.getName());
		if (eventTypeCatalogRef != null) {
			eventTypeCatalog = (EventTypeCatalog) context.getService(eventTypeCatalogRef);
		}

		initializeDsoaPlatform();

		EventType invocationEvent = eventTypeCatalog.get("InvocationEvent");
		mock = new EventProducerMock(invocationEvent);
	}

	@Configuration
	public Option[] config() {
		String configDir = "file:src/test/resources/config/";
		return options(felix(), bundle(configDir + "org.apache.felix.bundlerepository-1.6.2.jar"),
				bundle(configDir + "org.apache.felix.eventadmin-1.2.8.jar"), bundle(configDir
						+ "org.apache.felix.ipojo-1.8.0.jar"), bundle(configDir
						+ "org.apache.felix.ipojo.arch-1.6.0.jar"), bundle(configDir
						+ "org.apache.felix.ipojo.composite-1.6.0.jar"), bundle(configDir
						+ "org.apache.felix.ipojo.handler.eventadmin-1.8.0.jar"), bundle(configDir
						+ "org.apache.felix.ipojo.handler.whiteboard-1.6.0.jar"), bundle(configDir
						+ "org.apache.felix.shell-1.4.2.jar"), bundle(configDir
						+ "org.apache.felix.shell.tui-1.4.1.jar"), bundle(configDir
						+ "org.osgi.compendium-4.2.0.jar"),

				bundle(configDir + "dsoa/lib/antlr-runtime-3.1.1.jar"), bundle(configDir
						+ "dsoa/lib/cglib-nodep-2.2.jar"), bundle(configDir
						+ "dsoa/lib/commons-lang3-3.1.jar"), bundle(configDir
						+ "dsoa/lib/commons-logging-1.1.1.jar"), bundle(configDir
						+ "dsoa/lib/esper-4.7.0.jar"), bundle(configDir
						+ "dsoa/lib/monitoradmin-1.0.2.jar"),

				bundle(configDir + "dsoa/bin/dsoa-platform.jar"), bundle(configDir
						+ "dsoa/conf/configuration-bundle.jar")
						,vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
						new TimeoutOption(0)

		);
	}

	@Test
	public void testSubscriptionInvocationEvent() throws InvalidSyntaxException,
			FileNotFoundException, JAXBException {

		final int eventCounter = 10;

		String service = "stark", operation = "ned";
		final String source = String.format("%s.%s", service, operation);

		final EventType subscribedEventType = eventTypeCatalog.get("InvocationEvent");
		PropertyType propertyType = subscribedEventType.getMetadataPropertyType("source");
		EventFilter filter = HelperEpCenterTest.getEventFilter(propertyType, source, Expression.EQ);

		epCenter.subscribe(new EventConsumer() {

			@Override
			public void handleEvent(Event event) {
				System.out.println("====>> EVENT: ");
				System.out.println(event);
				org.junit.Assert.assertEquals(source, event.getMetadataProperty("source")
						.getValue());
			}

			@Override
			public String getId() {
				return "consumer-01";
			}
		}, new Subscription(subscribedEventType, filter), true);

		genTonsOffEvents(eventCounter, service, operation);
	}

	@Test
	public void testAgentSubscription() throws InvalidSyntaxException, FileNotFoundException,
			JAXBException {

		final int eventCounter = 10;

		String service = "stark", operation = "ned";
		final String source = String.format("%s.%s", service, operation);

		final EventType subscribedEventType = eventTypeCatalog.get("AvgResponseTimeEvent");
		PropertyType propertyType = subscribedEventType.getMetadataPropertyType("source");
		EventFilter filter = HelperEpCenterTest.getEventFilter(propertyType, source, Expression.EQ);

		epCenter.subscribe(new EventConsumer() {

			@Override
			public void handleEvent(Event event) {
				System.out.println("====>> EVENT: ");
				System.out.println(event);
				org.junit.Assert.assertEquals(source, event.getMetadataProperty("source")
						.getValue());
			}

			@Override
			public String getId() {
				return "consumer-01";
			}
		}, new Subscription(subscribedEventType, filter), false);

		genTimedEvent(eventCounter, service, operation);
	}

	private void initializeDsoaPlatform() throws JAXBException {

		Bundle[] bundles = context.getBundles();
		Bundle dsoa_bundle = null;
		for (Bundle bundle : bundles) {
			if ("dsoa-platform".equals(bundle.getSymbolicName())) {
				dsoa_bundle = bundle;
			}
		}
		HelperEpCenterTest.handleEventDefinitions(dsoa_bundle, epCenter, eventTypeCatalog);
		HelperEpCenterTest.handleAgentDefinitions(dsoa_bundle, epCenter);
	}

	private void genTonsOffEvents(int size, String service, String operation) {
		for (int i = 0; i < size; i++) {
			this.epCenter.publish(this.mock.getEvent(service, operation));
			this.epCenter.publish(this.mock.getEvent("stark", "jhon"));
			this.epCenter.publish(this.mock.getEvent("lannister", "tyrion"));
		}
	}

	private void genTimedEvent(int size, String service, String operation) {

		for (int i = 0; i < size; i++) {
			Event e;

			e = this.mock.getEvent(service, operation, i, i + 80);
			System.out.println(e);
			this.epCenter.publish(e);

			e = this.mock.getEvent("stark", "jhon", i, i + 1);
			System.out.println(e);
			this.epCenter.publish(e);

			e = this.mock.getEvent("lannister", "tyrion", i, i + 1);
			System.out.println(e);
			this.epCenter.publish(e);
		}
	}
}
