package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

import br.ufpe.cin.dsoa.api.event.FilterExpression;
import br.ufpe.cin.dsoa.api.event.Parameter;
import br.ufpe.cin.dsoa.api.service.Expression;
import br.ufpe.cin.dsoa.platform.registry.filter.AndFilter;
import br.ufpe.cin.dsoa.platform.registry.filter.FilterBuilder;
import br.ufpe.cin.dsoa.platform.registry.filter.ObjectFilter;

public class LdapFilterAdapter {

	private List<FilterExpression> expList;
	private BundleContext ctx;
	
	public LdapFilterAdapter(BundleContext ctx, List<FilterExpression> expList) {
		this.ctx = ctx;
		this.expList = expList;
	}

	@Override
	public String toString() {
		String filterStr = null;
		if (expList != null && !expList.isEmpty()) {
			Iterator<FilterExpression> itrExp = expList.iterator();
			Filter filter;
			try {
				filter = this.ctx.createFilter(new AndFilter(this.getFilters()).toString());
				filterStr = filter.toString();
			} catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return filterStr;
	}

	private List<FilterBuilder> getFilters() {
		List<FilterBuilder> filterList = new ArrayList<FilterBuilder>();
		for (FilterExpression exp : this.expList) {
			Parameter param = exp.getParameter();
			String paramName = null;
			Object paramValue = null;
			
			if (param != null) {
				paramName = param.getName();
				paramValue = param.getValue();
				Expression filterOp = exp.getExpression();
				filterList.add(new ObjectFilter(paramName,	filterOp, paramValue));
			}
		}
		
		return filterList;
	}

	/*
	 * List<FilterBuilder> filter = new ArrayList<FilterBuilder>();
		filter.add(new IFilter(Constants.OBJECTCLASS, spe));

		for (AttributeConstraint constraint : constraints) {
			filter.add(new DFilter(constraint.format(),	constraint.getExpression(), constraint.getThreashold()));
		}
		return filter;
		
		
		 context.createFilter(new AndFilter(this.getFilters(
					serviceInterface, constraints)).toString());
	 */
	
}
