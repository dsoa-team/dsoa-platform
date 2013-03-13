package br.ufpe.cin.dsoa.management;

import java.util.Map;

import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.epcenter.NotificationListener;
import br.ufpe.cin.dsoa.monitor.MetricInstance;

public class MetricMonitor implements NotificationListener {

	private MetricInstance instance;
	private Object value;

	public MetricMonitor(MetricInstance instance) {
		this.instance = instance;
	}
	
	public String getCategory() {
		return instance.getMetric().getCategory();
	}
	
	public String getName() {
		return instance.getMetric().getName();
	}
	
	public String getDescription() {
		return instance.getMetric().getDescription();
	}
	
	public String getTarget() {
		return instance.getTarget();
	}
	
	public StatusVariable getStatusVariable() {
		return new StatusVariable(getName(),StatusVariable.CM_GAUGE, value.toString());
	}

	public void receive(Map result, Object userObject, String statementName) {
		// TODO Auto-generated method stub
		
	}

	public void update(Object value) {
		System.out.println("Metric status: " + this.getName());
		System.out.println("Metric description: " + this.getDescription());
		this.value = value;
		System.out.println("Metric value: " + value);
	}
	
	public void receive(Object result, String statementName) {
		System.out.println("Metric status: " + this.getName());
		System.out.println("Metric description: " + this.getDescription());
		this.value = result;
		System.out.println("Metri value: " + value);
		System.out.println("Status variable: " + this.getStatusVariable());
		System.out.println("Status: " + this.getStatusVariable().getString());
		System.out.println("Status: " + this.getStatusVariable().getTimeStamp());
	}

	public String getServiceId() {
		return this.instance.getServiceId();
	}

	public String getOperationName() {
		return this.instance.getOperationName();
	}
}
