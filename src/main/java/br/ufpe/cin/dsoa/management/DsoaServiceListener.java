package br.ufpe.cin.dsoa.management;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.monitor.Monitorable;

import br.ufpe.cin.dsoa.DsoaConstants;
import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.utils.Util;

public class DsoaServiceListener {

	private BundleContext ctx;
	private ServiceListener sl;
	private List<ServiceRegistration> registrations;
	private EventProcessingCenter epCenter;
	private MetricCatalog metricCatalog;
	private Logger log;

	public DsoaServiceListener(BundleContext ctx) {
		this.ctx = ctx;
		this.registrations = new ArrayList<ServiceRegistration>();
		this.log = Logger.getLogger(getClass().getSimpleName());
	}

	public void start() {
		sl = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				ClassLoader cl = this.getClass().getClassLoader();
				if (event.getType() == ServiceEvent.REGISTERED) {
					ServiceReference reference = event.getServiceReference();
					if (Util.isRemote(reference)) {
						log.info("A new remote service was registered: " 
								+ reference.getProperty("service.id"));
						log.info("Creating a service proxy...");
						String[] classNames = (String[]) reference
								.getProperty(Constants.OBJECTCLASS);
						Class<?>[] classes = new Class<?>[classNames.length];
						int i = 0;
						for (String clazz : classNames) {
							try {
								classes[i++] = cl.loadClass(clazz);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
						Dictionary dict = copyProperties(reference);
						Object proxy = Proxy.newProxyInstance(cl, classes,
								new ServiceProxy(ctx, epCenter, reference));
						ServiceRegistration proxyRegistration = ctx
								.registerService(classNames, proxy, dict);
						log.info("Creating a service monitor...");
						ServiceMonitor monitor = new ServiceMonitor(epCenter, metricCatalog, reference);
						Hashtable ht = new Hashtable();
						ht.put("service.pid", reference.getProperty(Constants.SERVICE_PID) + "-Monitor");
						ServiceRegistration monitorRegistration = ctx.registerService(
						Monitorable.class.getName(), monitor, ht);
						
						registrations.add(proxyRegistration);
					}
				}
			}
		};

		try {
			ctx.addServiceListener(sl, getLdapRemoteServices());
		} catch (InvalidSyntaxException ex) {
			System.err.println("Activator: Cannot register service listener.");
			System.err.println("Activator: " + ex);
		}
	}

	public void stop() {
		ctx.removeServiceListener(sl);
		for (ServiceRegistration registration : registrations) {
			registration.unregister();
		}
	}

	private String getLdapRemoteServices() {
		System.out.println("teste...");
		return "(&(service.imported=*)(!(" + DsoaConstants.SERVICE_PROXY + "=*)))";
	}

	private Dictionary copyProperties(ServiceReference reference) {
		String[] keys = reference.getPropertyKeys();
		Dictionary dict = new Hashtable();
		for (String key : keys) {
			dict.put(key, reference.getProperty(key));
		}
		dict.put(DsoaConstants.SERVICE_PROXY, "true");
		return dict;
	}
}
