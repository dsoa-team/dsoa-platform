package br.ufpe.cin.dsoa.management;

import java.util.Map;

import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.DsoaConstants;
import br.ufpe.cin.dsoa.epcenter.NotificationListener;

public class StochasticVariableMonitor implements NotificationListener {

	private StochasticVariable variable;
	private Object value;

	public StochasticVariableMonitor(StochasticVariable variable) {
		this.variable = variable;
	}
	
	public String getCategory() {
		return variable.getMetric().getCategory();
	}
	
	public String getName() {
		return variable.getMetric().getName();
	}
	
	public String getScope() {
		return variable.getMetric().getScope();
	}

	public String getDescription() {
		return variable.getMetric().getDescription();
	}
	
	public String getPath() {
		return variable.getTarget() != null ? variable.getMetric() + DsoaConstants.TOKEN + variable.getTarget() : variable.getMetric().toString();
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
