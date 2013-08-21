package br.ufpe.cin.dsoa.configurator;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.configurator.hook.DsoaBundleListener;
import br.ufpe.cin.dsoa.event.EventProcessingService;

public class PlatformConfigurator {

	private DsoaBundleListener listener;
	private AttributeCatalog attributeCatalog;
	private EventProcessingService eventProcessingService;
	private BundleContext context;

	public PlatformConfigurator(BundleContext context) {
		this.context = context;
	}

	public void start() {
		listener = new DsoaBundleListener(context);
		listener.setEventProcessingService(eventProcessingService);
		listener.setMetricComputingService(attributeCatalog);
		listener.open();
	}

	public void stop() throws Exception {
		listener.close();
	}
}
