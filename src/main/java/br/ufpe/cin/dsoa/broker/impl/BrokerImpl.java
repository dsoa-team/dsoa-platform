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
import br.ufpe.cin.dsoa.broker.filter.AndFilter;
import br.ufpe.cin.dsoa.broker.filter.DFilter;
import br.ufpe.cin.dsoa.broker.filter.FilterBuilder;
import br.ufpe.cin.dsoa.broker.filter.IFilter;
import br.ufpe.cin.dsoa.broker.normalizer.Normalizer;
import br.ufpe.cin.dsoa.broker.rank.Rank;
import br.ufpe.cin.dsoa.contract.Slo;
import br.ufpe.cin.dsoa.handler.dependency.SelectionListener;
import br.ufpe.cin.dsoa.osgi.ServiceModelFactory;


public class BrokerImpl implements Broker {

	private BundleContext context;
	//private ServiceReference[] references;
	//private ServiceReference[] candidates;
	//private Filter filter;

	/**
	 * Constructor
	 * 
	 **/

	public BrokerImpl(BundleContext context) {
		this.context = context;
	}

	private List<FilterBuilder> getFilters(String spe, List<Slo> slos) {
		List<FilterBuilder> filter = new ArrayList<FilterBuilder>();
		filter.add(new IFilter(Constants.OBJECTCLASS, spe));
		for(Slo slo: slos) {
			if(slo.getOperation() != null) {
				filter.add(new DFilter(slo.getOperation() + "." + slo.getAttribute(), 
						slo.getExpression(), slo.getValue()));
			} else {
				filter.add(new DFilter(slo.getAttribute(), slo.getExpression(), slo.getValue()));
			}
		}
		return filter;
	}

	private ServiceReference findBestService(List<Slo> slos, List<ServiceReference> candidates) {
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
			Normalizer normalizer = new Normalizer(slos, references); // Entrou
			Rank rank = (Rank) context.getService(ranking);
			service = rank.ranking(slos, normalizer, references);
		}

		return service;
	}

	public void getBestService(String spe, List<Slo> slos,
			SelectionListener dep, List<ServiceReference> trash) {

		Filter filter = null;
		ServiceReference[] references = null;
		List<ServiceReference> candidates = null;
		List<ServiceReference> result = new ArrayList<ServiceReference>();
		try {
			filter = context.createFilter(new AndFilter(getFilters(
					spe, slos)).toString());

		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}

		//System.out.println("Filter:" + filter.toString());

		try {
			references = context.getServiceReferences(spe, new AndFilter(getFilters(
					spe, slos)).toString());
			if(references != null){
				candidates = Arrays.asList(references);
				//System.out.println("CANDIDATES ANTES: " + candidates.size());
				for(ServiceReference ref:candidates) {
					if (!trash.contains(ref)) {
						result.add(ref);
					}
				}
				candidates = result;
				//System.out.println("CANDIDATES DEPOIS: " + candidates.size());
				for(ServiceReference ref:candidates) {
					//System.out.println(ref);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(candidates == null || candidates.size() == 0) {
			ServiceTracker tracker = new BrokerTracker(dep, context, filter,trash);
			tracker.open();
			System.out.println("entrou no if --------");

		} else {
			//ServiceReference[] candidates = verifyBlackList(trash, references);
			ServiceReference reference = findBestService(slos, candidates);
			dep.notifySelection(ServiceModelFactory.createOsgiServiceModel(reference));
		}
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
