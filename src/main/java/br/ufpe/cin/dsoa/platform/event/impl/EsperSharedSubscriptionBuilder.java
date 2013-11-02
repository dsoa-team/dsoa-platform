package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Iterator;
import java.util.List;

import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.FilterExpression;
import br.ufpe.cin.dsoa.api.event.Subscription;

public class EsperSharedSubscriptionBuilder  implements QueryBuilder  {

		private Query query;
		private StringBuilder queryString;
		private Subscription subscription;

		public EsperSharedSubscriptionBuilder(Subscription subscription) {
			this.queryString = new StringBuilder();
			this.subscription = subscription;
		}

		public void buildSelectClause() {
			this.queryString.append("SELECT * ");
		}
		
		
		public void buildFromClause() {
			this.queryString.append(" FROM " + subscription.getEventType().getName());
			EventFilter filter  = subscription.getFilter();
			if (filter != null) {
				List<FilterExpression> expressions = filter.getFilterExpressions();
				boolean first = true;
				if (expressions != null) {
					Iterator<FilterExpression> iterator = expressions.iterator();
					while (iterator.hasNext()) {
						FilterExpression exp = iterator.next();
						if (!first) {
							this.queryString.append(" AND ");
						} else {
							this.queryString.append("(");
						}
						first = false;
						
						this.queryString.append(exp.getProperty().getPropertyType().getFullname());
						this.queryString.append(exp.getExpression().getOperator());
						Object value = exp.getProperty().getValue();
						if(exp.getProperty().getPropertyType().getType().equals(String.class)){
							this.queryString.append(String.format("'%s'", value));
						} else {
							this.queryString.append(String.format("%s", value));
						}
					}
					this.queryString.append(") ");
				}
			}
		}

		public void buildWhereClause() {
		}

		public void buildGroupByClause() {
		}

		public void buildHavingClause() {
		}

		public Query getQuery() {
			this.query = new Query(subscription.getId(), this.queryString.toString());
			return query;
		}

		@Override
		public void buildContextClause() {
			
		}

		@Override
		public void buildInsertIntoClause() {
			
		}

		@Override
		public void buildFilterClause() {
			
		}

		@Override
		public void buildWindowClause() {
			
		}

		@Override
		public void buildAliasClause() {
			
		}

	}
