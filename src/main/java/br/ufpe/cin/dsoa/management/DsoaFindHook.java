package br.ufpe.cin.dsoa.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.FindHook;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.event.InvocationEvent;
import br.ufpe.cin.dsoa.utils.Util;

public class DsoaFindHook implements EventHook, FindHook {

	private BundleContext ctx;
	private ServiceRegistration registration;
	private EventProcessingCenter epCenter;

	public DsoaFindHook(BundleContext context) {
		super();
		this.ctx = context;
	}

	public void start() {
		epCenter.defineEvent(InvocationEvent.class);
		registration = ctx.registerService(
				new String[] { FindHook.class.getName(),
						EventHook.class.getName() }, this, null);
	}

	public void stop() {
		epCenter.undefineEvent(InvocationEvent.class);
		registration.unregister();
	}
	
	public void find(BundleContext context, String name, String filter,
			boolean allServices, Collection references) {

		if (	   context.getBundle().getBundleId() == 0 
				|| context.getBundle().getBundleId() == 1
				|| context.getBundle().getBundleId() == 2
				|| context.getBundle().getBundleId() == 3
				|| context.getBundle().getBundleId() == 4
				|| context.getBundle().getBundleId() == 5
				|| context.getBundle().getBundleId() == 6
				|| context.getBundle().getBundleId() == 7
				|| context.getBundle().getBundleId() == 8
				|| context.getBundle().getBundleId() == 9
				|| context.getBundle().getBundleId() == 10
				|| context.getBundle().getBundleId() == 11
				|| context.getBundle().getBundleId() == 12
				|| context.getBundle().getBundleId() == 13) {
			return;
		}

		ClassLoader cl = this.getClass().getClassLoader();
		List<ServiceReference> proxies = new ArrayList<ServiceReference>(); 
		ServiceReference reference = null;
		for (Iterator<?> it = references.iterator(); it.hasNext();) {
			reference = (ServiceReference) it.next();
			if (Util.isRemote(reference) && !Util.isProxy(reference)) {
				references.remove(reference);
			}
		}
	}

	public void event(ServiceEvent event, Collection contexts) {
	}

}
