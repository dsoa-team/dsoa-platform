package br.ufpe.cin.dsoa.management.service.impl;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.configurator.hook.DsoaServiceListener;
import br.ufpe.cin.dsoa.management.service.ManagedService;
import br.ufpe.cin.dsoa.management.service.ServiceCatalog;

public class ServiceCatalogImpl implements ServiceCatalog {

	private BundleContext ctx;
	private DsoaServiceListener listener;
	private Map<String, ManagedService> managedServiceMap;
	
	public ServiceCatalogImpl(BundleContext ctx) {
		this.ctx = ctx;
		this.listener = new DsoaServiceListener(ctx);
		this.listener.start();
	}
	
	public void addService(ManagedService service) {

	}

}
