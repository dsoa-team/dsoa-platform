package br.ufpe.cin.dsoa.management;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Proxy;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

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
	private EventProcessingCenter epCenter;
	private MetricCatalog metricCatalog;
	private Logger log;
	private Map<String, ManagedService> managedServiceMap;
	
	public DsoaServiceListener(BundleContext ctx) {
		this.ctx = ctx;
		this.managedServiceMap = new ConcurrentHashMap<String, ManagedService>(8, 0.9f, 1);
		this.log = Logger.getLogger(getClass().getSimpleName());
	}

	public void start() {
		sl = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				ServiceReference reference;
				if (event.getType() == ServiceEvent.REGISTERED &&  Util.isRemote(reference = event.getServiceReference())) {
					String pid = reference.getProperty(Constants.SERVICE_ID).toString();
					
					ManagedService managedService = createManagedService(reference);
					managedServiceMap.put(pid, managedService);
				} else if (event.getType() == ServiceEvent.UNREGISTERING &&  Util.isRemote(reference = event.getServiceReference())) {
					// TODO: Unregister...
				}
			}
		};

		try {
			ctx.addServiceListener(sl, getLdapRemoteServicesFilter());
		} catch (InvalidSyntaxException ex) {
			System.err.println("Activator: Cannot register service listener.");
			System.err.println("Activator: " + ex);
		}
	}

	private ManagedService createManagedService(ServiceReference reference) {
		ServiceRegistration proxyRegistration = registerProxy(reference);
		ServiceRegistration monitorRegistration = registerMonitor(reference);
		ObjectInstance mbeanRegistration = registerMBean(reference);
		return new ManagedService(proxyRegistration, monitorRegistration, mbeanRegistration);
	}
	
	@SuppressWarnings("rawtypes")
	private ServiceRegistration registerProxy(ServiceReference reference) {
		ClassLoader cl = this.getClass().getClassLoader();

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
				new DsoaServiceProxy(ctx, epCenter, reference));
		return ctx.registerService(classNames, proxy, dict);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ServiceRegistration registerMonitor(ServiceReference reference) {
		log.info("Creating a service monitor...");
		DsoaServiceMonitor monitor = new DsoaServiceMonitor(epCenter,
				metricCatalog, reference);
		Hashtable ht = new Hashtable();
		ht.put("service.pid", reference.getProperty(Constants.SERVICE_PID)
				+ "-Monitor");
		return ctx.registerService(Monitorable.class.getName(),
				monitor, ht);
	}
	
	private ObjectInstance registerMBean(ServiceReference reference) {
		DsoaServiceMBean mbean = new DsoaServiceMBean(reference);
		String pid = reference.getProperty(Constants.SERVICE_ID).toString();
		ObjectName name;
		ObjectInstance instance = null;
		try {
			name = new ObjectName(DsoaConstants.SERVICE_SCOPE + ":pid=" + pid);
			instance = ManagementFactory.getPlatformMBeanServer().registerMBean(mbean, name);
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instance;
	}

	public void stop() {
		ctx.removeServiceListener(sl);
		// TODO Unregister...
	}

	private String getLdapRemoteServicesFilter() {
		return "(&(service.imported=*)(!(" + DsoaConstants.SERVICE_PROXY
				+ "=*)))";
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
