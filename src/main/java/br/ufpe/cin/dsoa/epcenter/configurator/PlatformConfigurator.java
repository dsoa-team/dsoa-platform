package br.ufpe.cin.dsoa.epcenter.configurator;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.epcenter.configurator.listener.BundleListener;
import br.ufpe.cin.dsoa.management.MetricCatalog;

public class PlatformConfigurator {

	private BundleListener listener;
	private MetricCatalog metricCatalog;
	private BundleContext context;

	public PlatformConfigurator(BundleContext context) {
		this.context = context;
	}

	public void start() {
		listener = new BundleListener(context);
		listener.setMetricCatalog(metricCatalog);
		listener.open();
	}

	public void stop() throws Exception {
		listener.close();
	}
}
