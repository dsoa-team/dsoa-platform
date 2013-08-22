package br.ufpe.cin.dsoa.handler.dependency.manager;

import java.util.List;

import br.ufpe.cin.dsoa.attribute.AttributableId;
import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.Goal;
import br.ufpe.cin.dsoa.mapper.AttributeAttributableMapper;

public class EsperVerifier implements Verifier {

	private AttributeCatalog attributeCatalog;
	private EventProcessingService eventProcessingService;
	
	public void configure(NotificationListener listener, String servicePid, List<Goal> constraints) {
		for (Goal constraint : constraints) {
			AttributeAttributableMapper instance = this.getMetricInstance(servicePid, constraint);
			String attId = instance.getAtttributeId();
			Attribute attribute = attributeCatalog.getAttribute(attId);
			//String statement = this.addFilters(attribute.getQuery(), servicePid, constraint);
			
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
	
	private AttributeAttributableMapper getMetricInstance(String servicePid, Goal goal) {
		String attId = goal.getAttributeId();
		String operationName = goal.getOperation();
		AttributeAttributableMapper instance = new AttributeAttributableMapper(attId, new AttributableId(servicePid, operationName));
		return instance;
	}

	private String buildStatement(String consumerId, String servicePid, Goal constraint) {
		// TODO Auto-generated method stub
		return null;
	}

}
