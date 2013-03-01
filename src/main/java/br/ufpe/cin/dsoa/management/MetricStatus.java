package br.ufpe.cin.dsoa.management;

import java.util.Map;

import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.epcenter.NotificationListener;

public class MetricStatus implements NotificationListener {

	private String name;
	private String description;
	private Object value;

	public MetricStatus(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public StatusVariable getStatusVariable() {
		System.out.println("Updatign status: ");
		System.out.println("==>> Name: " + getName());
		System.out.println("==>> Value: " + value);
		return new StatusVariable(getName(),StatusVariable.CM_GAUGE, value.toString());
	}

	public void receive(Map result, Object userObject, String statementName) {
		// TODO Auto-generated method stub
		
	}

	public void receive(Object result, String statementName) {
		System.out.println("Metric status: " + this.getName());
		System.out.println("Metric description: " + this.getDescription());
		this.value = result;
		System.out.println("Metri value: " + value);
		System.out.println("Status variable: " + this.getStatusVariable());
		System.out.println("Status: " + this.getStatusVariable().getString());
		System.out.println("Status: " + this.getStatusVariable().getTimeStamp());
		System.out.println("Status: " + this.getStatusVariable().getID());
	}
}
