package br.ufpe.cin.dsoa.epcenter;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.options;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.epcenter.helper.HelperEpCenterTest;

@RunWith( JUnit4TestRunner.class )
public class TestEventConsumer {

	@Inject
	private BundleContext context;

	@Inject
	private br.ufpe.cin.dsoa.platform.event.AgentCatalog plataformManagement;
	
	@Configuration
    public Option[] config() {
		String configDir = "file:src/test/resources/config/";
        return options(
        		felix(),
        		bundle(configDir + "org.apache.felix.bundlerepository-1.6.2.jar"),
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
	public void testEventConsumer(){
		ServiceReference epCenterRef = context.getServiceReference(br.ufpe.cin.dsoa.platform.event.EventProcessingService.class.getName());
		if (epCenterRef != null) {
			br.ufpe.cin.dsoa.platform.event.EventProcessingService epCenter = (br.ufpe.cin.dsoa.platform.event.EventProcessingService)context.getService(epCenterRef);
			epCenter.registerEventType(HelperEpCenterTest.getInvocationEventType());
			epCenter.subscribe(new EventConsumer() {
				
				@Override
				public void handleEvent(Event event) {
					System.out.println("===== CONSUMER: ======");
					System.out.println(event);
					org.junit.Assert.assertEquals(event.getEventType().getName(), HelperEpCenterTest.getInvocationEventType().getName());
				}
				
				@Override
				public String getId() {
					// TODO Auto-generated method stub
					return "consumer-01";
				}
			},new Subscription("sub-01", HelperEpCenterTest.getInvocationEventType(), null));
			epCenter.publish(HelperEpCenterTest.getSampleInvocationEvent());
			
		}
	}
}
