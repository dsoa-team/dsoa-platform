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

import br.ufpe.cin.dsoa.contract.Constants;
import br.ufpe.cin.dsoa.contract.Expression;
import br.ufpe.cin.dsoa.contract.ServiceConsumer;
import br.ufpe.cin.dsoa.contract.Slo;

public class DependencyHandler extends PrimitiveHandler {

	private List<ServiceDependency> dependencies = new ArrayList<ServiceDependency>();

	public void initializeComponentFactory(ComponentTypeDescription ctd,
			Element metadata) throws ConfigurationException {
		Element[] requiresElems = metadata.getElements(Constants.NAME,
				Constants.NAMESPACE);
		if (requiresElems.length != 1) {
			throw new ConfigurationException(
					"One and only one "
							+ Constants.NAME
							+ " element is allowed in component "
							+ ctd.getName()
							+ " configuration. "
							+ "use 'require' sub-elements to declare multiple dependencies.");
		}

		super.initializeComponentFactory(ctd, metadata);
	}

	private List<Slo> getSLOs(Element demand, Dictionary configuration) {
		List<Slo> sloList = new ArrayList<Slo>();
		Element[] sloSet = demand.getElements(Constants.SLO_ELEMENT);
		for (Element sloEle : sloSet) {
			// name
			String attribute = sloEle
					.getAttribute(Constants.SLO_ATTRIBUTE_ATTRIBUTE);

			// value
			double value = Double.parseDouble(sloEle
					.getAttribute(Constants.SLO_VALUE_ATTRIBUTE));

			// expression
			String expression = sloEle
					.getAttribute(Constants.SLO_EXPRESSION_ATTRIBUTE);

			// target
			String operation = sloEle
					.getAttribute(Constants.SLO_OPERATION_ATTRIBUTE);

			// statistic
			String statistic = sloEle
					.getAttribute(Constants.SLO_STATISTIC_ATTRIBUTE);

			// weight
			long weight = Long.parseLong(sloEle
					.getAttribute(Constants.SLO_WEIGHT_ATTRIBUTE));

			// window.value
			double windowValue = Double.parseDouble(sloEle
					.getAttribute(Constants.SLO_WINDOW_VALUE));

			// window.unit
			String windowUnit = sloEle.getAttribute(Constants.SLO_WINDOW_UNIT);

			Slo slo = new Slo(attribute, Expression.valueOf(expression), value,
					operation, statistic, weight, windowValue, windowUnit);

			sloList.add(slo);
		}
		return sloList;
	}
	

	private String getField(Element demand) throws ConfigurationException {
		return demand.getAttribute(Constants.SERVICE_FIELD_ATTRIBUTE);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void configure(Element metadata, Dictionary configuration)
			throws ConfigurationException {
		Element handlerConfig = metadata.getElements(Constants.NAME,
				Constants.NAMESPACE)[0];
		PojoMetadata manipulation = getFactory().getPojoMetadata();

		// Get consumer information
		String consumerPID = (String) handlerConfig
				.getAttribute(Constants.CONSUMER_PID_ATTRIBUTE);
		String consumerName = (String) handlerConfig
				.getAttribute(Constants.CONSUMER_NAME_ATTRIBUTE);

		Element[] serviceElems = handlerConfig
				.getElements(Constants.SERVICE_ELEMENT);

		for (Element service : serviceElems) {
			List<Slo> sloList = this.getSLOs(service, configuration);
			String field = this.getField(service);
			FieldMetadata fieldmeta = manipulation.getField(field);
			String specificationStr = fieldmeta.getFieldType();
			Class specification = DependencyModel.loadSpecification(
					specificationStr, getInstanceManager().getContext());

			ServiceDependency dependency = new ServiceDependency(this,
					new ServiceConsumer(consumerPID, consumerName),
					new DependencyMetadata(specification,sloList));
			
			this.register(fieldmeta, dependency);
			
		}
	}

	private void register(FieldMetadata fieldmeta, ServiceDependency dependency) {
		dependencies.add(dependency);
		getInstanceManager().register(fieldmeta, dependency);
		DependencyManager.createManager(dependency);
	}

	@Override
	public String toString() {
		return "DependencyHandler [dependencies=" + dependencies + "]";
	}

	@Override
	public void start() {
		this.setValidity(false);
		for (ServiceDependency dep : dependencies) {
			dep.start();
		}
	}

	@Override
	public void stop() {

	}

	public void checkValidate() {

		boolean valid = true;
		for (ServiceDependency dep : dependencies) {
			if (dep.isValid() == false) {
				valid = false;
				break;
			}
		}
		setValidity(valid);
	}

}
