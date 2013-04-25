package br.ufpe.cin.dsoa.broker.impl;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import br.ufpe.cin.dsoa.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.ServiceProvider;

public class BrokerTracker extends ServiceTracker {

	private ServiceListener qdl;
	private List<ServiceReference> blackList;
	
	public BrokerTracker(ServiceListener qdl, BundleContext context, Filter filter, List<ServiceReference> blackList) {
		super(context, filter, null);
		this.blackList = blackList;
		this.qdl = qdl;
	}	
	
	@Override
	public Object addingService(ServiceReference reference) {
		Object serviceObject = null;
		if (!blackList.contains(reference)) {
			String servicePid = (String)reference.getProperty(Constants.SERVICE_PID);
			serviceObject = context.getService(reference);
			qdl.onArrival(new ServiceProvider(servicePid, reference, serviceObject));
			ServiceTracker s =new ServiceTracker(context, reference, null) {
				@Override
				public void removedService(ServiceReference reference, Object object) {
					String servicePid = (String)reference.getProperty(Constants.SERVICE_PID);
					qdl.onDeparture(new ServiceProvider(servicePid, reference, object));
					super.removedService(reference, object);
					this.close();
				}
			};
			s.open();
			this.close();
		}
		return serviceObject;
	}
	
}
