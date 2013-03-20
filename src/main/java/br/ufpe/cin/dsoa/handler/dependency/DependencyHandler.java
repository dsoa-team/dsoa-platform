package br.ufpe.cin.dsoa.handler.dependency;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.architecture.ComponentTypeDescription;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.ServiceConsumer;
import br.ufpe.cin.dsoa.util.Constants;

public class DependencyHandler extends PrimitiveHandler {

	private List<DependencyManager> depMgrs = new ArrayList<DependencyManager>();
	private BundleContext ctx;

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

	private String getField(Element demand) throws ConfigurationException {
		return demand.getAttribute(Constants.SERVICE_FIELD_ATTRIBUTE);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void configure(Element metadata, Dictionary configuration)
			throws ConfigurationException {
		ctx = this.getInstanceManager().getContext();
		Element handlerConfig = metadata.getElements(Constants.NAME, Constants.NAMESPACE)[0];
		PojoMetadata manipulation = getFactory().getPojoMetadata();

		// Get consumer information
		String consumerPID = (String) handlerConfig
				.getAttribute(Constants.CONSUMER_PID_ATTRIBUTE);
		String consumerName = (String) handlerConfig
				.getAttribute(Constants.CONSUMER_NAME_ATTRIBUTE);
		
		ServiceConsumer serviceConsumer = new ServiceConsumer(consumerPID, consumerName);
		
		// Get service tags from the pojo's descriptor
		Element[] serviceElems = handlerConfig.getElements(Constants.SERVICE_ELEMENT);
		for (Element serviceElement : serviceElems) {
			DependencyManager dependencyMgr = this.getDependencyManager(serviceConsumer, serviceElement);
			String field = this.getField(serviceElement);
			FieldMetadata fieldmeta = manipulation.getField(field);
			this.register(fieldmeta, dependencyMgr);
		}
	}

	private DependencyManager getDependencyManager(ServiceConsumer serviceConsumer, Element serviceElement) {
		Element managerElement = serviceElement.getElements(Constants.MANAGER)[0];
		String managerName = null;
		if (managerElement != null) {
			managerName = managerElement.getAttribute("name");
			if (managerName == null) {
				managerName = "DependencyManager-Default";
			}
		}
		String filter = "name=" + managerName;
		ServiceReference factoryRef = null;
		try {
			factoryRef = ctx.getServiceReferences(DependencyManagerFactory.class.toString(), filter)[0];
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DependencyManagerFactory factory = (DependencyManagerFactory) ctx.getService(factoryRef);
		return factory.create(serviceConsumer, managerElement);
	}

	private void register(FieldMetadata fieldmeta, DependencyManager dependency) {
		depMgrs.add(dependency);
		getInstanceManager().register(fieldmeta, dependency);
	}

	@Override
	public String toString() {
		return "DependencyHandler [dependencies=" + depMgrs + "]";
	}

	@Override
	public void start() {
		this.setValidity(false);
		for (DependencyManager mgr : depMgrs) {
			mgr.start();
		}
	}

	@Override
	public void stop() {

	}

	public void checkValidate() {

		boolean valid = true;
		for (DependencyManager dep : depMgrs) {
			if (dep.isValid() == false) {
				valid = false;
				break;
			}
		}
		setValidity(valid);
	}

}
