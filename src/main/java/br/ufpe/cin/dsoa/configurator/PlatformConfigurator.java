package br.ufpe.cin.dsoa.configurator;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.configurator.hook.DsoaBundleListener;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.metric.MetricComputingService;

public class PlatformConfigurator {

	private DsoaBundleListener listener;
	private MetricComputingService metricComputingService;
	private EventProcessingService eventProcessingService;
	private BundleContext context;

	public PlatformConfigurator(BundleContext context) {
		this.context = context;
	}

	public void start() {
		listener = new DsoaBundleListener(context);
		listener.setEventProcessingService(eventProcessingService);
		listener.setMetricComputingService(metricComputingService);
		listener.open();
	}

	public void stop() throws Exception {
		listener.close();
	}
}
