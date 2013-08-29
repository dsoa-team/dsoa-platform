package br.ufpe.cin.dsoa.platform.registry.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.platform.registry.InvalidConstraintException;
import br.ufpe.cin.dsoa.platform.registry.filter.AndFilter;
import br.ufpe.cin.dsoa.platform.registry.filter.DFilter;
import br.ufpe.cin.dsoa.platform.registry.filter.FilterBuilder;
import br.ufpe.cin.dsoa.platform.registry.filter.IFilter;
import br.ufpe.cin.dsoa.platform.registry.normalizer.Normalizer;
import br.ufpe.cin.dsoa.platform.registry.rank.Rank;
import br.ufpe.cin.dsoa.service.AttributeConstraint;
import br.ufpe.cin.dsoa.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.service.Service;
import br.ufpe.cin.dsoa.service.ServiceSpecification;
import br.ufpe.cin.dsoa.service.impl.OsgiService;

public class OsgiServiceRegistry extends AbstractServiceRegistry {

	private BundleContext context;

	public OsgiServiceRegistry(BundleContext context) {
		this.context = context;
	}

	@Override
	public List<Service> findService(String serviceInterface,
			List<AttributeConstraint> constraints) {
		
		List<Service> services = new ArrayList<Service>();
		
		try {
			Filter filter = this.createFilter(serviceInterface, constraints);
			ServiceReference[] references = this.context.getServiceReferences(serviceInterface, filter.toString());
			for(ServiceReference reference : references){
				try {
					OsgiService service = new OsgiService(reference);
					services.add(service);
				} catch (ClassNotFoundException e) {
					logger.log(Level.WARNING, e.getMessage());
				}
			}
		} catch (InvalidConstraintException e) {
			logger.log(Level.WARNING, e.getMessage());
		} catch (InvalidSyntaxException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
		
		return services;
	}

	@Override
	public List<Service> findService(String serviceInterface) {
		
		List<Service> services = new ArrayList<Service>();
		
		ServiceReference reference = this.context.getServiceReference(serviceInterface);
		if(null != reference){
			try {
				services.add(new OsgiService(reference));
			} catch (ClassNotFoundException e) {
				logger.log(Level.WARNING, e.getMessage());
			}
		}
		return services;
	}

	@Override
	public Service rankServices(List<Service> services, List<AttributeConstraint> constraints) {
		
		Service service = null;
		
		List<ServiceReference> referenceList  =new ArrayList<ServiceReference>();
		for(Service s :  services){
			referenceList.add(((OsgiService) s).getReference());
		}
		
		ServiceReference[] references = referenceList.toArray(new ServiceReference[]{});
		ServiceReference ranking = context.getServiceReference(Rank.class.getName());
		ServiceReference reference = null;

		if (ranking == null) {
			Collections.sort(referenceList, new RankComparator());
			reference = referenceList.get(0);
		} else {
			Normalizer normalizer = new Normalizer(constraints, references); 
			Rank rank = (Rank) context.getService(ranking);
			reference = rank.ranking(constraints, normalizer, references);
		}
		
		try {
			service = new OsgiService(reference);
		} catch (ClassNotFoundException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
		return service;
	}

	@Override
	public void trackService(Service bestService, ServiceListener listener) {
		ServiceReference reference = ((OsgiService) bestService).getReference();
		this.openTracker(reference, listener);
	}

	@Override
	public void waitForService(ServiceSpecification specification,
			ServiceListener listener, List<String> blackList)
			throws InvalidConstraintException {

		Filter filter = null;

		NonFunctionalSpecification nonFunctionalSpecification = specification
				.getNonFunctionalSpecification();

		if (null != nonFunctionalSpecification) {
			List<AttributeConstraint> constraints = specification
					.getNonFunctionalSpecification().getAttributeConstraints();

			filter = this.createFilter(specification.getServiceInterface(),
					constraints);
			new OsgiTracker(context, filter, listener, blackList).open();
		}
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
				try {
					service = new OsgiService(reference);
					this.listener.onArrival(service);
					// open tracker for departures
					openTracker(reference, listener);
				} catch (ClassNotFoundException e) {
					OsgiServiceRegistry.this.registerError(e, listener);
				} finally {
					this.close();
				}
			}
			return service;
		}
	}

	private void openTracker(ServiceReference reference,
			final ServiceListener listener) {
		
		new ServiceTracker(this.context, reference, null) {
			@Override
			public void removedService(ServiceReference reference,
					Object service) {
				listener.onDeparture((Service) service);
				super.removedService(reference, service);
				this.close();
			}
		}.open();
	}

	private Filter createFilter(String serviceInterface,
			List<AttributeConstraint> constraints)
			throws InvalidConstraintException {

		Filter filter = null;
		try {
			filter = context.createFilter(new AndFilter(this.getFilters(
					serviceInterface, constraints)).toString());
		} catch (InvalidSyntaxException e) {
			throw new InvalidConstraintException("Invalid constraints!", e);
		}
		return filter;
	}

	private List<FilterBuilder> getFilters(String spe,
			List<AttributeConstraint> constraints) {
		List<FilterBuilder> filter = new ArrayList<FilterBuilder>();
		filter.add(new IFilter(Constants.OBJECTCLASS, spe));

		for (AttributeConstraint constraint : constraints) {
			if (constraint.getOperation() != null) {
				filter.add(new DFilter(Attribute.SERVICE_CONSTRAINT
						+ br.ufpe.cin.dsoa.util.Constants.TOKEN
						+ constraint.getAttributeId()
						+ br.ufpe.cin.dsoa.util.Constants.TOKEN
						+ constraint.getOperation(),
						constraint.getExpression(), constraint.getThreashold()));
			} else {
				filter.add(new DFilter(constraint.getAttributeId(), constraint
						.getExpression(), constraint.getThreashold()));
			}
		}
		return filter;
	}

}
