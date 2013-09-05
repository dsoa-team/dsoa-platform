package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import java.util.List;

import br.ufpe.cin.dsoa.api.attribute.AttributableId;
import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.event.NotificationListener;
import br.ufpe.cin.dsoa.api.service.AttributeConstraint;
import br.ufpe.cin.dsoa.platform.attribute.AttributeCatalog;
import br.ufpe.cin.dsoa.platform.event.EventProcessingService;

public class EsperVerifier implements Verifier {

	private AttributeCatalog attributeCatalog;
	private EventProcessingService eventProcessingService;
	
	public void configure(NotificationListener listener, String servicePid, List<AttributeConstraint> constraints) {
		for (AttributeConstraint constraint : constraints) {
			AttributeConstraint instance = this.getMetricInstance(servicePid, constraint);
			//String attId = instance.getAtttributeId();
			//Attribute attribute = attributeCatalog.getAttribute(attId);
			//String statement = this.addFilters(attribute.getQuery(), servicePid, constraint);
			
			//this.epService.defineStatement(name, statement)
			//Statement stmt = buildStatement(consumerId, servicePid, constraint);
			//this.epService.defineStatement(name, statement)
			System.out.println(constraint);
		}
	}
	
	private String addFilters(String query, String servicePid, AttributeConstraint constraint) {
		StringBuffer statement = new StringBuffer(query);
		statement.append(" ");
		statement.append(this.getThreasholdClause(constraint));
		return null;
	}

	private String getThreasholdClause(AttributeConstraint constraint) {
		return " ";
	}
	
	private AttributeConstraint getMetricInstance(String servicePid, AttributeConstraint attributeConstraint) {
		String attId = attributeConstraint.getAttributeId();
		String operationName = attributeConstraint.getOperation();
		//AttributeConstraint instance = new AttributeConstraint(attId, new AttributableId(servicePid, operationName));
		return null;
	}

	private String buildStatement(String consumerId, String servicePid, AttributeConstraint constraint) {
		// TODO Auto-generated method stub
		return null;
	}

}
