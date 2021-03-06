package br.ufpe.cin.dsoa.platform.registry.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import br.ufpe.cin.dsoa.api.selector.RankStrategy;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.RelationalOperator;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.api.service.impl.ServiceInstanceProxyImpl;
import br.ufpe.cin.dsoa.platform.component.autonomic.DsoaServiceListener;
import br.ufpe.cin.dsoa.platform.registry.InvalidServiceSpecificationException;
import br.ufpe.cin.dsoa.platform.registry.ServiceRegistry;
import br.ufpe.cin.dsoa.platform.registry.impl.filter.AndFilter;
import br.ufpe.cin.dsoa.platform.registry.impl.filter.DFilter;
import br.ufpe.cin.dsoa.platform.registry.impl.filter.FilterBuilder;
import br.ufpe.cin.dsoa.platform.registry.impl.filter.IFilter;
import br.ufpe.cin.dsoa.platform.registry.impl.filter.ObjectFilter;

public class OsgiServiceRegistry implements ServiceRegistry {

	private BundleContext context;

	public OsgiServiceRegistry(BundleContext context) {
		this.context = context;
	}

	public final void getBestService(ServiceSpecification specification,
			List<String> blackList, DsoaServiceListener listener) {

		ServiceReference[] references = this.getServiceReferences(
				specification, blackList);

		if (references != null) {
			ServiceInstance bestService = this.rankServices(specification
					.getFunctionalInterface().getInterfaceName(), references, specification
					.getNonFunctionalSpecification().getConstraints());
			
			this.trackService(bestService, listener);
			listener.onArrival(bestService);
		} else {
			this.waitForService(specification, listener, blackList);
		}
	}

	private ServiceReference[] getServiceReferences(ServiceSpecification spec,
			List<String> blackList) throws InvalidServiceSpecificationException {
		String serviceInterface = spec.getFunctionalInterface().getInterfaceName();
		if (serviceInterface == null) {
			throw new InvalidServiceSpecificationException(
					"Invalid service interface", serviceInterface);
		}

		try {
			Filter filter = this.createFilter(spec, blackList);
			return this.context.getServiceReferences(serviceInterface,
					filter.toString());
		} catch (InvalidSyntaxException e) {
			throw new InvalidServiceSpecificationException(
					"Invalid service specification", spec, e);
		}
	}

	/**
	 * Filter out service that do not support the required QoS characteristics or
	 * that are part of a black list.
	 * @param spec
	 * @param blackList
	 * @return
	 */
	private Filter createFilter(ServiceSpecification spec,
			List<String> blackList) {
		NonFunctionalSpecification nfs = spec.getNonFunctionalSpecification();
		List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
		addServiceInterfaceFilter(filterBuilders, spec.getFunctionalInterface().getInterfaceName());
		addConstraintFilters(filterBuilders, nfs);
		addBlackListFilters(filterBuilders, blackList);
		try {
			return context.createFilter(new AndFilter(filterBuilders)
					.toString());
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			throw new InvalidServiceSpecificationException(
					"Invalid service specification",
					spec.getFunctionalInterface().getInterfaceName(),
					(spec.getNonFunctionalSpecification() == null ? null : spec
							.getNonFunctionalSpecification()
							.getConstraints()));
		}
	}

	private void addServiceInterfaceFilter(List<FilterBuilder> filterBuilders,
			String serviceInterface) {
		filterBuilders
				.add(new IFilter(Constants.OBJECTCLASS, serviceInterface));
	}

	private void addConstraintFilters(List<FilterBuilder> filterBuilders,
			NonFunctionalSpecification nfs) {
		if (nfs != null) {
			List<Constraint> constraints = nfs
					.getConstraints();
			if (constraints != null && !constraints.isEmpty()) {
				for (Constraint constraint : constraints) {
					filterBuilders.add(new DFilter(constraint.format(),
							constraint.getExpression(), constraint
									.getThreashold()));
				}
			}
		}
	}

	private void addBlackListFilters(List<FilterBuilder> filterBuilders,
			List<String> blackList) {
		if (blackList != null && !blackList.isEmpty()) {
			for (String serviceId : blackList) {
				filterBuilders.add(new ObjectFilter(Constants.SERVICE_ID,
						RelationalOperator.NE, serviceId));
			}
		}
	}

	private ServiceInstance rankServices(String serviceInterface,
			ServiceReference[] references, List<Constraint> requiredConstraints) {
		
		List<ServiceReference> referenceList = Arrays.asList(references);

		List<ServiceInstance> candidates = new ArrayList<ServiceInstance>();
		for(ServiceReference reference : referenceList) {
			candidates.add(DsoaOsgiUtils.translateOsgiServiceToDsoa(serviceInterface, reference, true));
		}
		
		ServiceReference ranking = context.getServiceReference(RankStrategy.class.getName());
		ServiceInstance selectedService = null;
		
		if(ranking != null){
			RankStrategy strategy = (RankStrategy) context.getService(ranking);
			selectedService = strategy.ranking(requiredConstraints, candidates);
		} else {
			selectedService = candidates.get(0);
		}
		
		return selectedService;
	}

	public void trackService(ServiceInstance bestService, DsoaServiceListener listener) {
		ServiceReference reference = ((ServiceInstanceProxyImpl) bestService).getServiceReference();
		this.openTracker(reference, listener);
	}

	@Override
	public void waitForService(ServiceSpecification specification,
			DsoaServiceListener listener, List<String> blackList)
			throws InvalidServiceSpecificationException {
		new OsgiTracker(context, this.createFilter(specification, blackList),
				listener, specification.getFunctionalInterface().getInterfaceName(), blackList).open();
	}

	private void openTracker(ServiceReference reference,
			final DsoaServiceListener listener) {

		new ServiceTracker(this.context, reference, null) {
			@Override
			public void removedService(ServiceReference reference,
					Object service) {
				// MODIFICAR PARA REFERENCIAR UMA SERVICE INSTANCE
				listener.onDeparture(null);
				super.removedService(reference, service);
				this.close();
			}
		}.open();
	}

	protected void registerError(Exception e, DsoaServiceListener listener) {
		listener.onError(e);
	}

	/**
	 * This tracker is used only when the BindingManager asks the Service Registry to get a 
	 * service and there is no available service candidates
	 * 
	 * @author fabions
	 *
	 */
	class OsgiTracker extends ServiceTracker {

		private DsoaServiceListener listener;
		private String serviceItf;
		private List<String> blackList;

		public OsgiTracker(BundleContext context, Filter filter,
				DsoaServiceListener listener, String serviceItf, List<String> blackList) {
			super(context, filter, null);
			this.serviceItf = serviceItf;
			this.listener = listener;
			this.blackList = blackList;
		}

		public Object addingService(ServiceReference reference) {

			ServiceInstance service = null;

			if (!blackList.contains(reference)) {
				//TODO MODIFICAR PARA REFERENCIAR UMA SERVICE INSTANCE
				service = DsoaOsgiUtils.translateOsgiServiceToDsoa(
						serviceItf, reference, true);
				this.listener.onArrival(service);
				// open tracker for departures
				openTracker(reference, listener);
				this.close();
			}
			return service;
		}
	}
}
