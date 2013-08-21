package br.ufpe.cin.dsoa.handler.dependency.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import br.ufpe.cin.dsoa.attribute.AttributableId;
import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.attribute.AttributeId;
import br.ufpe.cin.dsoa.attribute.mappers.AttributeAttributableMapper;
import br.ufpe.cin.dsoa.configurator.parser.attribute.Attribute;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.Goal;
import br.ufpe.cin.dsoa.util.Constants;

public class EsperVerifier implements Verifier {

	private AttributeCatalog attributeCatalog;
	private EventProcessingService epService;
	
	public void configure(NotificationListener listener, String servicePid, List<Goal> constraints) {
		for (Goal constraint : constraints) {
			AttributeAttributableMapper instance = this.getMetricInstance(servicePid, constraint);
			AttributeId attId = instance.getAtttributeId();
			Attribute attribute = attributeCatalog.getAttribute(attId);
			String statement = this.addFilters(attribute.getQuery(), servicePid, constraint);
			
			//this.epService.defineStatement(name, statement)
			//Statement stmt = buildStatement(consumerId, servicePid, constraint);
			//this.epService.defineStatement(name, statement)
			System.out.println(constraint);
		}
	}
	
	private String addFilters(String query, String servicePid, Goal constraint) {
		StringBuffer statement = new StringBuffer(query);
		statement.append(" ");
		statement.append(this.getThreasholdClause(constraint));
		return null;
	}

	private String getThreasholdClause(Goal constraint) {
		return " ";
	}
	
	private AttributeAttributableMapper getMetricInstance(String servicePid, Goal constraint) {
		// prefix.category.metric.target
		// metric.QoS.ResponseTime.priceAlert
		//[metric=qos.ResponseTime, operation=priceAlert, windowType=LENGTH, windowSize=20, expression=LT, threashold=800.0, weight=2]
		List<String> nameParts = new ArrayList<String>();
		String metricName = constraint.getMetric();
		StringTokenizer st = new StringTokenizer(metricName, Constants.TOKEN);
		while (st.hasMoreTokens()) {
			nameParts.add(st.nextToken());
		}
		
		AttributeId attributeId = new AttributeId(nameParts.get(0), nameParts.get(1));
		AttributeAttributableMapper instance = null;
		String operationName = constraint.getOperation();
		instance = new AttributeAttributableMapper(attributeId, new AttributableId(servicePid, operationName));
		return instance;
	}

	private String buildStatement(String consumerId, String servicePid, Goal constraint) {
		// TODO Auto-generated method stub
		return null;
	}

}
