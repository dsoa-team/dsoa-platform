package br.ufpe.cin.dsoa.platform.registry.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.api.service.Expression;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.Service;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.api.service.impl.OsgiService;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.platform.registry.InvalidServiceSpecificationException;
import br.ufpe.cin.dsoa.platform.registry.ServiceRegistry;
import br.ufpe.cin.dsoa.platform.registry.filter.AndFilter;
import br.ufpe.cin.dsoa.platform.registry.filter.DFilter;
import br.ufpe.cin.dsoa.platform.registry.filter.FilterBuilder;
import br.ufpe.cin.dsoa.platform.registry.filter.IFilter;
import br.ufpe.cin.dsoa.platform.registry.filter.ObjectFilter;
import br.ufpe.cin.dsoa.platform.registry.normalizer.Normalizer;
import br.ufpe.cin.dsoa.platform.registry.rank.Rank;
import br.ufpe.cin.dsoa.util.DsoaSimpleLogger;

public class OsgiServiceRegistry implements ServiceRegistry {

	private BundleContext context;

	protected static Logger logger = DsoaSimpleLogger.getDsoaLogger(
			ServiceRegistry.class.getName(), true, true);

	public OsgiServiceRegistry(BundleContext context) {
		this.context = context;
	}

	public final void getBestService(ServiceSpecification specification,
			List<String> blackList, ServiceListener listener) {

		logger.log(Level.INFO, "blackList: {0}", blackList.toArray());
		ServiceReference[] references = this.getServiceReferences(
				specification, blackList);

		if (references != null) {
			Service bestService = this.rankServices(specification
					.getServiceInterface(), references, specification
					.getNonFunctionalSpecification().getAttributeConstraints());
			listener.onArrival(bestService);
			this.trackService(bestService, listener);
		} else {
			this.waitForService(specification, listener, blackList);
		}
	}

	private ServiceReference[] getServiceReferences(ServiceSpecification spec,
			List<String> blackList) throws InvalidServiceSpecificationException {
		String serviceInterface = spec.getServiceInterface();
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

	private Filter createFilter(ServiceSpecification spec,
			List<String> blackList) {
		NonFunctionalSpecification nfs = spec.getNonFunctionalSpecification();
		List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
		addServiceInterfaceFilter(filterBuilders, spec.getServiceInterface());
		addConstraintFilters(filterBuilders, nfs);
		addBlackListFilters(filterBuilders, blackList);
		try {
			return context.createFilter(new AndFilter(filterBuilders)
					.toString());
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			throw new InvalidServiceSpecificationException(
					"Invalid service specification",
					spec.getServiceInterface(),
					(spec.getNonFunctionalSpecification() == null ? null : spec
							.getNonFunctionalSpecification()
							.getAttributeConstraints()));
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
			List<AttributeConstraint> constraints = nfs
					.getAttributeConstraints();
			if (constraints != null && !constraints.isEmpty()) {
				for (AttributeConstraint constraint : constraints) {
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
						Expression.NE, serviceId));
			}
		}
	}

	private Service rankServices(String serviceInterface,
			ServiceReference[] references, List<AttributeConstraint> constraints) {
		List<ServiceReference> referenceList = Arrays.asList(references);
		ServiceReference ranking = context.getServiceReference(Rank.class
				.getName());

		ServiceReference reference = null;
		if (ranking == null) {
			Collections.sort(referenceList, new RankComparator());
			reference = referenceList.get(0);
		} else {
			Normalizer normalizer = new Normalizer(constraints, references);
			Rank rank = (Rank) context.getService(ranking);
			reference = rank.ranking(constraints, normalizer, references);
		}
		return OsgiService.getOsgiService(serviceInterface, reference);
	}

	public void trackService(Service bestService, ServiceListener listener) {
		ServiceReference reference = ((OsgiService) bestService).getReference();
		this.openTracker(reference, listener);
	}

	@Override
	public void waitForService(ServiceSpecification specification,
			ServiceListener listener, List<String> blackList)
			throws InvalidServiceSpecificationException {
		new OsgiTracker(context, this.createFilter(specification, blackList),
				listener, blackList).open();
	}

	private void openTracker(ServiceReference reference,
			final ServiceListener listener) {

		new ServiceTracker(this.context, reference, null) {
			@Override
			public void removedService(ServiceReference reference,
					Object service) {
				listener.onDeparture(OsgiService.getOsgiService(
						listener.getServiceInterface(), reference));
				super.removedService(reference, service);
				this.close();
			}
		}.open();
	}

	protected void registerError(Exception e, ServiceListener listener) {
		logger.log(Level.SEVERE, e.getMessage());
		listener.onError(e);
	}

	class OsgiTracker extends ServiceTracker {

		private ServiceListener listener;
		private List<String> blackList;

		public OsgiTracker(BundleContext context, Filter filter,
				ServiceListener listener, List<String> blackList) {
			super(context, filter, null);

			this.listener = listener;
			this.blackList = blackList;
		}

		public Object addingService(ServiceReference reference) {

			Service service = null;

			if (!blackList.contains(reference)) {
				service = OsgiService.getOsgiService(
						listener.getServiceInterface(), reference);
				this.listener.onArrival(service);
				// open tracker for departures
				openTracker(reference, listener);
				this.close();
			}
			return service;
		}
	}
}
