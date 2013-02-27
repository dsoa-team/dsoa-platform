package br.ufpe.cin.dsoa.epcenter.configurator;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.epcenter.configurator.listener.BundleListener;

public class PlatformConfigurator {

	private BundleListener listener;
	private EventProcessingCenter epCenter;
	private BundleContext context;

	public PlatformConfigurator(BundleContext context) {
		this.context = context;
	}

	public void start() {
		listener = new BundleListener(context);
		listener.setEventProcessingCenter(epCenter);
		listener.open();
	}

	public void stop() throws Exception {
		listener.close();
	}
}
