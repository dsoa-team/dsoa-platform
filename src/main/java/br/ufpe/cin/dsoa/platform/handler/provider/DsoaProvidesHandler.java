package br.ufpe.cin.dsoa.platform.handler.provider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.architecture.ComponentTypeDescription;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceHandler;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;

import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.DsoaComponentInstance;
import br.ufpe.cin.dsoa.api.service.DsoaComponentType;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.ProvidedPort;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.api.service.impl.NonFunctionalSpecificationImpl;
import br.ufpe.cin.dsoa.api.service.impl.ProvidedPortImpl;
import br.ufpe.cin.dsoa.api.service.impl.ServiceInstanceImpl;
import br.ufpe.cin.dsoa.api.service.impl.ServiceSpecificationImpl;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentFactory;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentInstanceManager;
import br.ufpe.cin.dsoa.platform.component.DsoaConstraintParser;
import br.ufpe.cin.dsoa.util.Constants;


public class DsoaProvidesHandler extends PrimitiveHandler implements ManagedService {

	private ServiceRegistration registration;
	private ProviderMetadata p_metadata;
	private List<ServiceInstance> serviceInstanceList = new ArrayList<ServiceInstance>();
	private DsoaProvidesHandlerDescription description;

	 /**
     * Initialize the component type.
     * 
     * @param desc : component type description to populate.
     * @param metadata : component type metadata.
     * 
     * @throws ConfigurationException : occurs when the POJO does not implement any interfaces.
     * @see org.apache.felix.ipojo.Handler#initializeComponentFactory(org.apache.felix.ipojo.architecture.ComponentTypeDescription, org.apache.felix.ipojo.metadata.Element)
     */
    public void initializeComponentFactory(ComponentTypeDescription desc, Element metadata) throws ConfigurationException {
    	DsoaComponentFactory dsoaFactory = (DsoaComponentFactory)this.getFactory();
		DsoaComponentType dsoaComponentType = dsoaFactory.getComponentType();
		
		Element[] providesTags = metadata.getElements(Constants.PROVIDES_TAG, Constants.PROVIDES_TAG_NAMESPACE);
		for (Element providesTag : providesTags) {
			String name = (String) providesTag.getAttribute(Constants.PROVIDES_ATT_FIELD);
			String itfName = (String)providesTag.getAttribute(Constants.PROVIDES_ATT_CLASSNAME);
			
			List<Constraint> constraintList = DsoaConstraintParser.getConstraintList(providesTag.getElements(Constants.CONSTRAINT_TAG),null);
			NonFunctionalSpecification nonFunctionalSpecification = new NonFunctionalSpecificationImpl(constraintList);
			
			ServiceSpecification serviceSpecification =  new ServiceSpecificationImpl(itfName, nonFunctionalSpecification);
			ProvidedPortImpl providedPort = new ProvidedPortImpl(name, serviceSpecification);
			dsoaComponentType.addProvidedPort(providedPort);
		}
    }
	
	
	@Override
	public void configure(Element metadata,
			@SuppressWarnings("rawtypes") Dictionary configuration)
			throws ConfigurationException {

		DsoaComponentInstanceManager manager = (DsoaComponentInstanceManager)this.getInstanceManager();
		DsoaComponentInstance componentInstance = manager.getDsoaComponentInstance();
		DsoaComponentType componentType = componentInstance.getComponentType();
		
		/*
		 * It is important to see that we can only create Bindings here
		 * since in the initializeComponentFactory only the ComponentType
		 * was available, not the ComponentInstance.
		 */
		for(ProvidedPort providedPort : componentType.getProvidedPortList()) {
			ServiceInstance serviceInstance = new ServiceInstanceImpl(this, componentInstance, providedPort, configuration);
			this.serviceInstanceList .add(serviceInstance);
		}
		
		description = new DsoaProvidesHandlerDescription(this); 
		manager.getInstanceDescription().addHandler(this.getDescription());
	}

	@Override
	public void stop() {
	}

	@Override
	public void start() {
		for (ServiceInstance instance : this.serviceInstanceList) {
			List<String> propertyNames = instance.getPropertyNames();
			Dictionary<String,String> dictionary = new Hashtable<String, String>();
			for (String propertyName : propertyNames) {
				Object value = instance.getProperty(propertyName);
				dictionary.put(propertyName, value.toString());
			}
			
		}
	}

	@Override
	public void onEntry(Object arg0, Method arg1, Object[] arg2) {
		System.err.println("ON ENTRY");
	}

	@Override
	public void onError(Object arg0, Method arg1, Throwable arg2) {
		System.err.println("ON ERROR");
	}

	@Override
	public void onExit(Object arg0, Method arg1, Object arg2) {
		System.err.println("ON EXIT");
	}

	@Override
	public Object onGet(Object pojo, String fieldName, Object value) {
		System.err.println("ON GET");
		return super.onGet(pojo, fieldName, value);
	}

	@SuppressWarnings("rawtypes")
	public void updated(Dictionary arg0)
			throws org.osgi.service.cm.ConfigurationException {

		registration.setProperties(arg0);

		ProvidedServiceHandler handlerProvider = (ProvidedServiceHandler) super
				.getInstanceManager().getHandler(
						"org.apache.felix.ipojo:provides");

		if (null != handlerProvider) {
			handlerProvider.addProperties(arg0);
		}

	}
	
	public ProviderMetadata getProviderMetadata() {
		return this.p_metadata;
	}
	
	public List<ServiceInstance> getServiceInstances() {
		return new ArrayList<ServiceInstance>(this.serviceInstanceList);
	}
	
	@Override
	public HandlerDescription getDescription() {
		return this.description;
	}
}
