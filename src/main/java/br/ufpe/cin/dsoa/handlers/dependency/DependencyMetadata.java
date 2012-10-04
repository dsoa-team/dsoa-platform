package br.ufpe.cin.dsoa.handlers.dependency;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.contract.Constants;
import br.ufpe.cin.dsoa.contract.Expression;
import br.ufpe.cin.dsoa.contract.Slo;

public class DependencyMetadata {
	
	private String field;
	private List<Slo> slos;
	
	
	public DependencyMetadata() {
	}
	
	@SuppressWarnings("rawtypes")
	public void createMetadata(Element service, Dictionary configuration) throws ConfigurationException {   
		this.slos = new ArrayList<Slo>();
		// Initializes field
		buildAttributes(service);

		// SLOs
		buildSLOs(service, configuration);

	}
	
	private void buildAttributes(Element demand) throws ConfigurationException {
		// field attribute
		setField(demand.getAttribute(Constants.SERVICE_FIELD_ATTRIBUTE));

	}
	
	@SuppressWarnings("rawtypes")
	private void buildSLOs(Element demand, Dictionary configuration){
		
			Element[] sloSet = demand.getElements(Constants.SLO_ELEMENT);
			for (Element sloEle : sloSet){

					//name
					String attribute = sloEle.getAttribute(Constants.SLO_ATTRIBUTE_ATTRIBUTE);
					
					//value
					double value = Double.parseDouble(sloEle.getAttribute(Constants.SLO_VALUE_ATTRIBUTE));
					
					//expression
					String expression = sloEle.getAttribute(Constants.SLO_EXPRESSION_ATTRIBUTE);
					
					//target
					String operation = sloEle.getAttribute(Constants.SLO_OPERATION_ATTRIBUTE);
					
					// statistic
					String statistic = sloEle.getAttribute(Constants.SLO_STATISTIC_ATTRIBUTE);
					
					//weight
					long weight = Long.parseLong(sloEle.getAttribute(Constants.SLO_WEIGHT_ATTRIBUTE));
					
					//window.value
					double windowValue = Double.parseDouble(sloEle.getAttribute(Constants.SLO_WINDOW_VALUE));
					
					//window.unit
					String windowUnit = sloEle.getAttribute(Constants.SLO_WINDOW_UNIT);
					
					Slo  slo = new Slo(attribute, Expression.valueOf(expression), value, operation, statistic, weight, windowValue, windowUnit);
					
					getSlos().add(slo);
			}
		}

	public void setField(String field) {
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public void reset() {
		setField(null);
		setSlos(null);
	}

	
	
	public List<Slo> getSlos() {
		return slos;
	}

	public void setSlos(List<Slo> slos) {
		this.slos = slos;
	}
	
	
	

}
