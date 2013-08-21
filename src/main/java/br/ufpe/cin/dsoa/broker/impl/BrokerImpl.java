package br.ufpe.cin.dsoa.broker.impl;

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

import br.ufpe.cin.dsoa.broker.Broker;
import br.ufpe.cin.dsoa.broker.InvalidConstraintException;
import br.ufpe.cin.dsoa.broker.filter.AndFilter;
import br.ufpe.cin.dsoa.broker.filter.DFilter;
import br.ufpe.cin.dsoa.broker.filter.FilterBuilder;
import br.ufpe.cin.dsoa.broker.filter.IFilter;
import br.ufpe.cin.dsoa.broker.normalizer.Normalizer;
import br.ufpe.cin.dsoa.broker.rank.Rank;
import br.ufpe.cin.dsoa.configurator.parser.attribute.Attribute;
import br.ufpe.cin.dsoa.handler.dependency.ServiceListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.Goal;
import br.ufpe.cin.dsoa.handler.dependency.contract.ServiceProvider;


public class BrokerImpl implements Broker {

	public void getBestService(BundleContext context, String specification, List<Goal> constraints, 
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
			final ServiceProvider provider = new ServiceProvider(context, reference);
			listener.onArrival(provider);
			ServiceTracker s = new ServiceTracker(context, reference, null) {
				@Override
				public void removedService(ServiceReference reference, Object object) {
					listener.onDeparture(provider);
					super.removedService(reference, object);
					this.close();
				}
			};
			s.open();
		}
	}
	
	private List<FilterBuilder> getFilters(String spe, List<Goal> constraints) {
		List<FilterBuilder> filter = new ArrayList<FilterBuilder>();
		filter.add(new IFilter(Constants.OBJECTCLASS, spe));
		//metric.QoS.ResponseTime.priceAlert
		for(Goal constraint: constraints) {
			if(constraint.getOperation() != null) {
				filter.add(new DFilter(Attribute.ATTRIBUTE_PREFIX +constraint.getMetric() + "." + constraint.getOperation(), 
						constraint.getExpression(), constraint.getThreashold()));
			} else {
				filter.add(new DFilter(constraint.getMetric(), constraint.getExpression(), constraint.getThreashold()));
			}
		}
		return filter;
	}

	private ServiceReference findBestService(BundleContext context, List<Goal> constraints, List<ServiceReference> candidates) {
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
