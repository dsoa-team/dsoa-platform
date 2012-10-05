package br.ufpe.cin.dsoa.handlers.dependency;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.architecture.ComponentTypeDescription;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;
import org.apache.felix.ipojo.util.DependencyModel;
import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.contract.Constants;

public class DependencyHandler extends PrimitiveHandler {

	private String consumerPID;
	private String consumerName;
	private String qosMode;
	private List<ServiceDependency> dependencies = new ArrayList<ServiceDependency>();
	
	public void initializeComponentFactory(ComponentTypeDescription ctd, Element metadata) throws ConfigurationException {
		Element[] requiresElems = metadata.getElements(Constants.NAME, Constants.NAMESPACE);
		if (requiresElems.length != 1) {
			throw new ConfigurationException("One and only one "+Constants.NAME+" element is allowed in component "+ctd.getName()+" configuration. "
					+"use 'require' sub-elements to declare multiple dependencies.");
		}

		super.initializeComponentFactory(ctd, metadata);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void configure(Element metadata, Dictionary configuration)
			throws ConfigurationException {
		Element      requires     = metadata.getElements(Constants.NAME, Constants.NAMESPACE)[0];
		PojoMetadata manipulation = getFactory().getPojoMetadata();
		
		// Get consumer information
		consumerPID = (String)  requires.getAttribute(Constants.CONSUMER_PID_ATTRIBUTE);
		consumerName = (String) requires.getAttribute(Constants.CONSUMER_NAME_ATTRIBUTE);
		qosMode = requires.getAttribute(Constants.QOS_MODE);
		
		Element[] serviceElems = requires.getElements(Constants.SERVICE_ELEMENT);
		DependencyMetadata dm = new DependencyMetadata();
		
		for (Element service : serviceElems) {
			dm.reset();
			dm.createMetadata(service, configuration);
			
			String field            = dm.getField();
			FieldMetadata fieldmeta = manipulation.getField(field);

			String svcInterface = fieldmeta.getFieldType();
			Class specification = DependencyModel.loadSpecification(svcInterface, getInstanceManager().getContext());
			
			ServiceDependency dependency = new ServiceDependency(this, specification, dm.getSlos());
			dependencies.add(dependency);
			// register the service field
			getInstanceManager().register(fieldmeta, dependency);
		}
	}

	@Override
	public String toString() {
		return "DependencyHandler [consumerPID=" + consumerPID
				+ ", consumerName=" + consumerName + ", qosMode=" + qosMode
				+ ", dependencies=" + dependencies + "]";
	}

	@Override
	public void start() {
		this.setValidity(false);
		System.out.println("TESTE..............");
		for (ServiceDependency dep : dependencies) {
			System.out.println("TESTE 22222222222222222");
			dep.start();
			System.out.println("TESTE 3333333333333333333333");
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	public void checkValidate(){
		
		boolean valid = true;
		for(ServiceDependency dep : dependencies){
			if(dep.isValid() == false){
				valid = false;
				break;
			}
		}
		setValidity(valid);
	}

	public BundleContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getConsumerPID() {
		return consumerPID;
	}

	public String getConsumerName() {
		return consumerName;
	}

	public String getQosMode() {
		return qosMode;
	}
}
