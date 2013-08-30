package br.ufpe.cin.dsoa.platform.monitor;

import java.util.Map;
import java.util.logging.Logger;

import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.attribute.AttributableId;
import br.ufpe.cin.dsoa.attribute.Attribute;
import br.ufpe.cin.dsoa.attribute.meta.AttributeType;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.util.Constants;

/**
 * Responsible for storing the metricValue of a attribute for an specific target (service.operation). It receives updates directly from
 * the Event Processing Service.
 * 
 * @author fabions
 */
public class MonitoredAttribute implements NotificationListener {
	
	private String				statusVariableId;	
	private AttributeType 			attribute;
	private AttributableId	 	attributableId;
	private Attribute 		attributeValue;
	private Logger 				log;
	
	public MonitoredAttribute(AttributableId attributableId, AttributeType attribute) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.attributableId = attributableId;
		this.attribute = attribute;
	}
	
	public String getAttributeName() {
		return this.attribute.getName();
	}
	
	public String getAttributeDescription() {
		return this.attribute.getDescription();
	}
	
	public String getAttributableId() {
		return this.attributableId.getId();
	}
	
	public String getStatusVariableId() {
		if (statusVariableId == null) {
			statusVariableId = this.attribute.getId() + Constants.TOKEN + this.attributableId.toString();
		}
		return statusVariableId;
	}
	
	public StatusVariable getStatusVariable() {
		return new StatusVariable(getStatusVariableId(),StatusVariable.CM_GAUGE, attributeValue.toString());
	}
	
	public void receive(Map result, Object userObject, String statementName) {
		// TODO Auto-generated method stub
	}

	public void update(Object value) {
		log.info("Property status: " + this.getAttributeName());
		log.info("Property description: " + this.getAttributeDescription());
		//this.attributeValue = this.attribute.getAttributeEventMapper().mapEventAttribute((Event)value);
		log.info("Property metricValue: " + value);
	}
	
	public void receive(Object result, String statementName) {
		log.info("Property status: " + this.getAttributeName());
		log.info("Property description: " + this.getAttributeDescription());
		//this.attributeValue = this.attribute.getAttributeEventMapper().mapEventAttribute((Event)result);
		log.info("Metri metricValue: " + result);
		log.info("Status variable: " + this.getStatusVariable());
		log.info("Status: " + this.getStatusVariable().getString());
		log.info("Status: " + this.getStatusVariable().getTimeStamp());
	}

}
