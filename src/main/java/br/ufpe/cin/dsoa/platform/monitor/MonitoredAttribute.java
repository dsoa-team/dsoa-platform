package br.ufpe.cin.dsoa.platform.monitor;

import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.monitor.MonitorListener;
import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.api.attribute.AttributableId;
import br.ufpe.cin.dsoa.api.attribute.Attribute;
import br.ufpe.cin.dsoa.api.attribute.AttributeChangeListener;
import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
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
	private String			 	monitoredServicePid;
	private Logger 				log;
	private BundleContext 		ctx;
	private MonitoringRegistration monitoringRegistration;
	
	public MonitoredAttribute(BundleContext ctx, String monitoredServicePid, AttributableId attributableId, Attribute attribute) {
		this.log = Logger.getLogger(getClass().getSimpleName());
		this.attributableId = attributableId;
		this.attribute = attribute;
		this.monitoredServicePid = monitoredServicePid;
		this.ctx = ctx;
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
			statusVariableId = this.attributableId.getOperationName() + Constants.TOKEN + this.attribute.getName();
		}
		return statusVariableId;
	}
	
	public StatusVariable getStatusVariable() {
		String value = attributeValue.getValue().toString();
		return new StatusVariable(getStatusVariableId(),StatusVariable.CM_GAUGE, value);
	}
	
	@Override
	public void update(AttributeValue value) {
		this.attributeValue = value;
		this.getMonitorListener().updated(this.monitoredServicePid, this.getStatusVariable());
	}
	
	private MonitorListener getMonitorListener(){
		MonitorListener monitorListener = null;
		ServiceReference monitorListenerRef = ctx.getServiceReference(MonitorListener.class.getName());
		
		if(monitorListenerRef != null){
			monitorListener = (MonitorListener) ctx.getService(monitorListenerRef);
		}
		
		return monitorListener;
	}

	public void setMonitoringRegistration(MonitoringRegistration monitoringRegistration) {
		this.monitoringRegistration = monitoringRegistration;
	}

	public MonitoringRegistration getMonitoringRegistration() {
		return monitoringRegistration;
	}
	
}
