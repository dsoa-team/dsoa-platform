package br.ufpe.cin.dsoa.broker.impl;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import br.ufpe.cin.dsoa.handler.dependency.ServiceListener;

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
		if (!blackList.contains(reference)) {
			qdl.onArrival(reference);
			ServiceTracker s =new ServiceTracker(context, reference, null) {
				@Override
				public void removedService(ServiceReference reference, Object object) {
					qdl.onDeparture(reference);
					super.removedService(reference, object);
					this.close();
				}
			};
			s.open();
			this.close();
		}
		return reference;
	}
	
}
