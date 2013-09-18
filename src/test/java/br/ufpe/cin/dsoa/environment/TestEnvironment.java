package br.ufpe.cin.dsoa.environment;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.platform.configurator.DsoaServiceTracker;

@RunWith( JUnit4TestRunner.class )
public class TestEnvironment {

	@Inject
	private BundleContext context;

	@Inject
	private DsoaServiceTracker tracker;
	
	@Configuration
    public Option[] config() {
		String configDir = "file:src/test/resources/config/";
        return options(
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
        		bundle(configDir + "dsoa/conf/configuration-bundle.jar"),
        		
        		junitBundles()
        		
            );
    }
	
	@Test
    public void getDsoaServiceTracker() {
        assertNotNull(tracker);
    }
}
