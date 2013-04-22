package br.ufpe.cin.dsoa.broker;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.Constraint;
import br.ufpe.cin.dsoa.handler.dependency.ServiceListener;



/**
 * 
 * @author David
 **/

public interface Broker {
	
	public void getBestService(BundleContext context, String specification, List<Constraint> constraints, 
			List<ServiceReference> blackList, ServiceListener listener);
	
}