package br.ufpe.cin.dsoa.configurator;

import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.configurator.hook.DsoaBundleListener;
import br.ufpe.cin.dsoa.mapper.AttributeEventMapperCatalog;

public class PlatformConfigurator {

	private DsoaBundleListener listener;
	private AttributeCatalog attributeCatalog;
	private AttributeEventMapperCatalog attributeEventMapperCatalog;
	private BundleContext context;

	public PlatformConfigurator(BundleContext context) {
		this.context = context;
	}

	public void start() {
		listener = new DsoaBundleListener(this.context);
		listener.setAttributeCatalog(this.attributeCatalog);
		listener.setAttributeEventMapperCatalog(this.attributeEventMapperCatalog);
		listener.open();
	}

	public void stop() throws Exception {
		listener.close();
	}
}
