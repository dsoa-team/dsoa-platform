package br.ufpe.cin.dsoa.broker;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.Goal;



/**
 * 
 * @author David
 **/

public interface Broker {
	
	public void getBestService(BundleContext context, String specification, List<Goal> constraints, 
			List<ServiceReference> blackList, ServiceListener listener);
	
}