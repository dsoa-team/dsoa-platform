package br.ufpe.cin.dsoa.configurator;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.configurator.hook.DsoaBundleListener;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.metric.MetricCatalog;

public class PlatformConfigurator {

	private DsoaBundleListener listener;
	private MetricCatalog metricCatalog;
	private EventProcessingService epCenter;
	private BundleContext context;

	public PlatformConfigurator(BundleContext context) {
		this.context = context;
	}

	public void start() {
		listener = new DsoaBundleListener(context);
		listener.setEventProcessingCenter(epCenter);
		listener.setMetricCatalog(metricCatalog);
		listener.open();
	}

	public void stop() throws Exception {
		listener.close();
	}
}
