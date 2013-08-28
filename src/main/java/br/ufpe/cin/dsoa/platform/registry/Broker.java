package br.ufpe.cin.dsoa.platform.registry;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.service.AttributeConstraint;

/**
 * 
 * @author David
 **/

public interface Broker {
	
	public void getBestService(BundleContext context, String specification, List<AttributeConstraint> constraints, 
			List<ServiceReference> blackList, ServiceListener listener);
	
}