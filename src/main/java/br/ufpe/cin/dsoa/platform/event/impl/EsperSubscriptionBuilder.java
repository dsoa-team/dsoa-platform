package br.ufpe.cin.dsoa.platform.event.impl;

import java.util.Iterator;
import java.util.List;

import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventFilter;
import br.ufpe.cin.dsoa.api.event.FilterExpression;
import br.ufpe.cin.dsoa.api.event.PropertyType;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.event.agent.EventProcessingAgent;
import br.ufpe.cin.dsoa.util.Constants;

public class EsperSubscriptionBuilder extends EsperAgentBuilder  {

		private Query query;
		private Subscription subscription;
		private EventConsumer eventConsumer;

		public EsperSubscriptionBuilder(EventConsumer eventConsumer, Subscription subscription, 
				EventProcessingAgent eventProcessingAgent) {
			
			super(eventProcessingAgent);
			this.subscription = subscription;
			this.eventConsumer = eventConsumer;
		}

		public void buildSelectClause() {
			super.buildSelectClause();
			this.queryString.append( ", '" + out.getType() + "' as " + Constants.EVENT_METADATA +
					Constants.UNDERLINE + Constants.EVENT_TYPE);
		}

		
		public void buildContextClause() {
			
		}
		
		public void buildInsertIntoClause() {
			
		}
		
		public void buildFilterClause(){
			this.queryString.append("(");
			this.queryString.append(this.in.getAlias() +".data_consumerId = '" + eventConsumer.getId() +"'");
			this.queryString.append(" ) ");
		}
		
		public void buildWhereClause() {
		}

		public void buildGroupByClause() {
		}

		public void buildHavingClause() {

			this.queryString.append(" HAVING ");
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
						
						
						PropertyType propertyType = exp.getProperty().getPropertyType();
						String namesapce = propertyType.getNamespace();
						String alias = in.getAlias();
						
						String expression = null;
						if(namesapce.equalsIgnoreCase(Constants.EVENT_DATA)){
							Iterator<PropertyType> types = this.out.getData().iterator();
							while(types.hasNext()){
								PropertyType type = types.next();
								if(type.getName().equals(propertyType.getName())){
									expression = type.getExpression();
								}
							}
									
						} else if(namesapce.equalsIgnoreCase(Constants.EVENT_METADATA)) {
							Iterator<PropertyType> types = this.out.getMetadata().iterator();
							while(types.hasNext()){
								PropertyType type = types.next();
								if(type.getName().equals(propertyType.getName())){
									expression = type.getExpression();
								}
							}
						}
						
						if(expression != null) {
							expression = this.parseExpression(expression, alias);
							this.queryString.append(expression);
							
							this.queryString.append(exp.getExpression().getOperator());
							Object value = exp.getProperty().getValue();
							if(exp.getProperty().getPropertyType().getType().equals(String.class)){
								this.queryString.append(String.format("'%s'", value));
							} else {
								this.queryString.append(String.format("%s", value));
							}
						}
					}
					this.queryString.append(") ");
				}
			}
		}

		public Query getQuery() {
			this.query = new Query(subscription.getId(), this.queryString.toString());
			return query;
		}

	}
