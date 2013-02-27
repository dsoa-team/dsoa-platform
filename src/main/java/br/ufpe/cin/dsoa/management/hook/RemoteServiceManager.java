package br.ufpe.cin.dsoa.management.hook;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.FindHook;

public class RemoteServiceManager implements EventHook, FindHook{

	BundleContext ctx;
	public ServiceRegistration registration;
	
	public RemoteServiceManager(BundleContext context) {
		super();
		this.ctx = context;
	}

	void start() {
		System.out.println("Funcionou!!111onze");
		registration = ctx.registerService(
				new String[] {FindHook.class.getName(), EventHook.class.getName()}, this, null);	
	}
	
	public void find(BundleContext context, String name, String filter,
			boolean allServices, Collection references) {
		
		if(ctx.equals(context) || context.getBundle().getBundleId() == 0) {
			return;
		}
		
		ClassLoader cl = this.getClass().getClassLoader();
		for(Iterator<?> it = references.iterator(); it.hasNext(); ) {
			ServiceReference reference = (ServiceReference) it.next();
			if(null != reference.getProperty("service.imported")) {
				String[] classNames = (String[]) reference.getProperty(Constants.OBJECTCLASS);
				Class<?>[] classes = new Class<?>[classNames.length];
				int i = 0;
				for(String clazz : classNames) {
					try {
						classes[i++] = cl.loadClass(clazz);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				Object proxy = Proxy.newProxyInstance(cl, classes, new ServiceProxy(ctx, reference));
				//ctx.registerService(classNames, proxy, properties)
			}
		}
	}

	public void event(ServiceEvent event, Collection contexts) {}
	
}
