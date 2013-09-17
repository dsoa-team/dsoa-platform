package br.ufpe.cin.dsoa.platform.monitor;

import java.util.Map;
import java.util.logging.Logger;

import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.api.attribute.AttributableId;
import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.attribute.AttributeChangeListener;
import br.ufpe.cin.dsoa.util.Constants;

/**
 * Responsible for storing the metricValue of a attribute for an specific target (service.operation). It receives updates directly from
 * the Event Processing Service.
 * 
 * @author fabions
 */
public class MonitoredAttribute implements AttributeChangeListener {
	
	private String				statusVariableId;	
	private Attribute 			attribute;
	private AttributableId	 	attributableId;
	private AttributeValue 		attributeValue;
	private Logger 				log;
	
	public MonitoredAttribute(AttributableId attributableId, Attribute attribute) {
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
			statusVariableId = this.attributableId.toString() + Constants.TOKEN + this.attribute.getId();
		}
		return statusVariableId;
	}
	
	public StatusVariable getStatusVariable() {
		return new StatusVariable(getStatusVariableId(),StatusVariable.CM_GAUGE, attributeValue.toString());
	}
	
	@Override
	public void update(AttributeValue value) {
		this.attributeValue = value;
	}
}
