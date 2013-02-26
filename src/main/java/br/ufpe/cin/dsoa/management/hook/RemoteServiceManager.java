package br.ufpe.cin.dsoa.management.hook;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.FindHook;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.event.InvocationEvent;
import br.ufpe.cin.dsoa.event.InvocationEventOld;

public class RemoteServiceManager implements EventHook, FindHook {

	BundleContext ctx;
	public ServiceRegistration registration;
	private EventProcessingCenter epCenter;

	public RemoteServiceManager(BundleContext context) {
		super();
		this.ctx = context;
	}

	void start() {
		epCenter.defineEvent(InvocationEvent.class);
		registration = ctx.registerService(
				new String[] { FindHook.class.getName(),
						EventHook.class.getName() }, this, null);
	}

	public void find(BundleContext context, String name, String filter,
			boolean allServices, Collection references) {

		if (ctx.equals(context) || context.getBundle().getBundleId() == 0) {
			return;
		}

		ClassLoader cl = this.getClass().getClassLoader();
		for (Iterator<?> it = references.iterator(); it.hasNext();) {
			ServiceReference reference = (ServiceReference) it.next();
			if (null != reference.getProperty("service.imported")) {
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

				String[] keys = reference.getPropertyKeys();
				Dictionary dict = new Hashtable();

				for (String key : keys) {
					dict.put(key, reference.getProperty(key));
				}
				Object proxy = Proxy.newProxyInstance(cl, classes,
						new ServiceProxy(ctx, epCenter, reference));
				ctx.registerService(classNames, proxy, dict);
				references.remove(reference);
			}
		}
	}

	public void event(ServiceEvent event, Collection contexts) {
	}

}
