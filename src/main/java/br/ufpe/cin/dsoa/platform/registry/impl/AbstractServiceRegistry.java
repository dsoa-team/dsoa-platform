package br.ufpe.cin.dsoa.platform.registry.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.platform.monitor.DynamicProxyFactory;
import br.ufpe.cin.dsoa.platform.registry.InvalidConstraintException;
import br.ufpe.cin.dsoa.platform.registry.ServiceRegistry;

public abstract class AbstractServiceRegistry implements ServiceRegistry {

	private Map<String, Service> serviceMap = new HashMap<String, Service>();
	protected static Logger logger = Logger.getLogger(ServiceRegistry.class
			.getName());

	public final synchronized boolean addService(Service service) {
		boolean result = false;
		String id = service.getServiceId();
		if (!serviceMap.containsKey(id)) {
			serviceMap.put(id, service);
			result = true;
		}
		return result;
	}

	public final Service getService(String id) {
		return serviceMap.get(id);
	}

	public final void getBestService(ServiceSpecification specification,
			List<String> blackList, ServiceListener listener) {

		String serviceInterface = specification.getServiceInterface();
		NonFunctionalSpecification nfs = specification
				.getNonFunctionalSpecification();
		
		List<AttributeConstraint> constraints = null;
		if(null != nfs){
			constraints = nfs.getAttributeConstraints();
		}
		
		List<Service> services  = this.findCandidates(serviceInterface, constraints, blackList);

		if (services.isEmpty()) {
			try {
				this.waitForService(specification, listener, blackList);
			} catch (InvalidConstraintException e) {
				this.registerError(e, listener);
			}
		} else {
			Service bestService = this.rankServices(serviceInterface, services, nfs.getAttributeConstraints());
			//DynamicProxyFactory proxyFactory = new DynamicProxyFactory(bestService);
			//listener.onArrival(proxyFactory.getProxy());
			listener.onArrival(bestService);
			this.trackService(bestService, listener);
		}
	}

	protected void registerError(Exception e, ServiceListener listener) {
		logger.log(Level.SEVERE, e.getMessage());
		listener.onError(e);
	}

	public List<Service> getServices() {
		return new ArrayList<Service>(this.serviceMap.values());
	}

	public List<Service> getServices(String[] classNames) {
		return null;
	}

	public abstract void waitForService(ServiceSpecification specification,
			ServiceListener listener, List<String> blackList) throws InvalidConstraintException;

	public abstract List<Service> findService(String serviceInterface,
			List<AttributeConstraint> contraints);

	public abstract List<Service> findService(String serviceInterface);

	public abstract Service rankServices(String serviceInterface, List<Service> services, List<AttributeConstraint> constraints);

	public abstract void trackService(Service bestService,
			ServiceListener listener);

	private void filterServices(List<String> blackList, List<Service> services) {
		if (null != blackList && !blackList.isEmpty()) {
			services.removeAll(blackList);
		}
	}

	/**
	 * This method find service candidates by functional or non-functional
	 * specifications.
	 * 
	 * @param serviceInterface
	 *            - functional specification
	 * @param nfs
	 *            - non-functional specifications
	 * @return List of candidates services
	 */
	private List<Service> findCandidates(String serviceInterface,
			List<AttributeConstraint> constraints, List<String> blackList) {

		List<Service> services = new ArrayList<Service>();

		
		if (null != constraints) {
			services = this.findService(serviceInterface, constraints);
		} else {
			services = this.findService(serviceInterface);
		}

		// filter black listed services
		this.filterServices(blackList, services);

		return services;
	}

}
