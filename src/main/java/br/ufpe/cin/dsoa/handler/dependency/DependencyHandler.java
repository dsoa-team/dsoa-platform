package br.ufpe.cin.dsoa.handler.dependency;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.handlers.dependency.DependencyHandlerDescription;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.ServiceConsumer;
import br.ufpe.cin.dsoa.util.Constants;

public class DependencyHandler extends PrimitiveHandler {

	private List<Dependency> deps = new ArrayList<Dependency>();
	private boolean valid;
	private DependencyHandlerDescription m_description;
	
	/*
	 * 
	 * <qos:requires>
                 <service field="homebroker">
                 	  <manager id="QoSDependencyManager" >
	                      <qos metric="ResponseTime" 
	                      	  expression="LT" 
	                      	  value="800" 
	                      	  weight="2" 
	                      	  operation="priceAlert" />
	                      		 	
	                      <qos metric="Availability" 
	                      	  expression="GT" 
	                      	  value="95" 
	                      	  weight="1" /> 
	                    </manager>
                 </service>
           </qos:requires>
	 */
	
	@SuppressWarnings("rawtypes")
	@Override
	public void configure(Element metadata, Dictionary configuration)
			throws ConfigurationException {
		String consumerName = metadata.getAttribute(Constants.NAME_ATT);
		String consumerId 	= metadata.getAttribute(Constants.ID_ATT);
		ServiceConsumer serviceConsumer = new ServiceConsumer(consumerId, consumerName);
		
		Element requiresTag = metadata.getElements(Constants.NAME, Constants.NAMESPACE)[0];
		String  field 		= (String) requiresTag.getAttribute(Constants.FIELD_ATT);
		/*<qos category="qos"
       	  metric="ResponseTime" 
       	  operation="priceAlert" 
       	  expression="LT" 
       	  value="800" 
       	  weight="2"  />*/
		Element[] qosTags	= requiresTag.getElements(Constants.QOS_ATT);
		String category = null;
		String metric = null;
		String operation = null;
		String expression = null;
		String value = null;
		String weight = null;
		for (Element qosTag : qosTags) {
			category = qosTag.getAttribute(Constants.CATEGORY_ATT);
			metric = qosTag.getAttribute(Constants.METRIC_ATT);
			operation = qosTag.getAttribute(Constants.OPERATION_ATT);
			expression = qosTag.getAttribute(Constants.EXPRESSION_ATT);
			value = qosTag.getAttribute(Constants.VALUE_ATT);
			weight = qosTag.getAttribute(Constants.WEIGHT_ATT);
		}
	}

	private void register(FieldMetadata fieldmeta, Dependency dependency) {
		deps.add(dependency);
		getInstanceManager().register(fieldmeta, dependency);
	}

	@Override
	public String toString() {
		return "DependencyHandler [dependencies=" + deps + "]";
	}

	@Override
	public void start() {
		this.setValidity(false);
		for (Dependency mgr : deps) {
			mgr.start();
		}
	}

	@Override
	public void stop() {

	}

	public void checkValidate() {

		boolean valid = true;
		for (Dependency dep : deps) {
			if (dep.isValid() == false) {
				valid = false;
				break;
			}
		}
		setValidity(valid);
	}

}
