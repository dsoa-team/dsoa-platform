package br.ufpe.cin.dsoa.platform.registry.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.platform.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.platform.registry.Broker;
import br.ufpe.cin.dsoa.platform.registry.InvalidConstraintException;
import br.ufpe.cin.dsoa.platform.registry.filter.AndFilter;
import br.ufpe.cin.dsoa.platform.registry.filter.DFilter;
import br.ufpe.cin.dsoa.platform.registry.filter.FilterBuilder;
import br.ufpe.cin.dsoa.platform.registry.filter.IFilter;
import br.ufpe.cin.dsoa.platform.registry.normalizer.Normalizer;
import br.ufpe.cin.dsoa.platform.registry.rank.Rank;
import br.ufpe.cin.dsoa.service.AttributeConstraint;
import br.ufpe.cin.dsoa.service.Service;
import br.ufpe.cin.dsoa.service.impl.OsgiService;


public class BrokerImpl implements Broker {

	public void getBestService(BundleContext context, String specification, List<AttributeConstraint> constraints, 
			List<ServiceReference> blackList, final ServiceListener listener) {

		ServiceReference[] references = null;
		List<ServiceReference> candidates = null;
		List<ServiceReference> result = new ArrayList<ServiceReference>();
		Filter filter = null;
		try {
			filter = context.createFilter(new AndFilter(getFilters(
				specification, constraints)).toString());
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			throw new InvalidConstraintException("Invalid constraints!", e);
		} 
		
		try {
			references = context.getServiceReferences(specification, filter.toString());
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			throw new InvalidConstraintException("Invalid constraints!", e);
		} 
		
		if(references != null){
			candidates = Arrays.asList(references);
			for(ServiceReference ref:candidates) {
				if (!blackList.contains(ref)) {
					result.add(ref);
				}
			}
			candidates = result;
		}
		

		if(candidates == null || candidates.size() == 0) {
			ServiceTracker tracker = new BrokerTracker(listener, context, filter, blackList);
			tracker.open();
		} else {
			//ServiceReference[] candidates = verifyBlackList(trash, references);
			ServiceReference reference = findBestService(context, constraints, candidates);
			final Service service = new OsgiService(reference);
			listener.onArrival(service);
			ServiceTracker s = new ServiceTracker(context, reference, null) {
				@Override
				public void removedService(ServiceReference reference, Object object) {
					listener.onDeparture(service);
					super.removedService(reference, object);
					this.close();
				}
			};
			s.open();
		}
	}
	
	private List<FilterBuilder> getFilters(String spe, List<AttributeConstraint> constraints) {
		List<FilterBuilder> filter = new ArrayList<FilterBuilder>();
		filter.add(new IFilter(Constants.OBJECTCLASS, spe));
		//metric.QoS.ResponseTime.priceAlert
		for(AttributeConstraint constraint: constraints) {
			if(constraint.getOperation() != null) {
				filter.add(new DFilter(Attribute.SERVICE_CONSTRAINT + br.ufpe.cin.dsoa.util.Constants.TOKEN  + constraint.getAttributeId() + br.ufpe.cin.dsoa.util.Constants.TOKEN + constraint.getOperation(), 
						constraint.getExpression(), constraint.getThreashold()));
			} else {
				filter.add(new DFilter(constraint.getAttributeId(), constraint.getExpression(), constraint.getThreashold()));
			}
		}
		return filter;
	}

	private ServiceReference findBestService(BundleContext context, List<AttributeConstraint> constraints, List<ServiceReference> candidates) {
		ServiceReference service = null;
		ServiceReference[] references = candidates.toArray(new ServiceReference[candidates.size()]);
		//double[][] norm = normalizer.normalizedMatrix(slos, candidates);
		ServiceReference ranking = context.getServiceReference(Rank.class.getName());

		if(ranking == null) {
			Collections.sort(candidates, new RankComparator());
			service = candidates.get(0);
			//System.out.println(">>>>>>>>>>>>>>>  ========  getCotation.ResponseTime: " + service.getProperty("getCotation.ResponseTime"));
			//System.out.println("Properties");
			//for(String chave: service.getPropertyKeys()){
				//System.out.println(chave + " - " +service.getProperty(chave));
			//}
			//service = references[0];

		} else {
			Normalizer normalizer = new Normalizer(constraints, references); // Entrou
			Rank rank = (Rank) context.getService(ranking);
			service = rank.ranking(constraints, normalizer, references);
		}

		return service;
	}

	/*
	private ServiceReference[] verifyBlackList(List<ServiceReference> trash,
			ServiceReference... candidates) {
		trash.
		int cont = 0;
		ServiceReference[] references = new ServiceReference[candidates.length] ;

			if((trash != null) && (!trash.isEmpty())) {
				for(int i=0; i<candidates.length; i++) {
					for(int j=0; j<trash.size(); j++) {

						if(trash.contains(candidates[i])){
						//if(candidates[i].equals(trash.get(i))) {
							references[cont++] = candidates[i];
							break;
						}				
					}
				}

			} else {
				references = candidates;
			}  

		return references;
	}
	 */
}
