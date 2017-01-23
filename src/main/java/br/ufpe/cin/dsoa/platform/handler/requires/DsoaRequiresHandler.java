package br.ufpe.cin.dsoa.platform.handler.requires;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;

import br.ufpe.cin.dsoa.api.service.Binding;
import br.ufpe.cin.dsoa.api.service.DsoaComponentInstance;
import br.ufpe.cin.dsoa.api.service.DsoaComponentType;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.RequiredPort;
import br.ufpe.cin.dsoa.api.service.impl.BindingImpl;
import br.ufpe.cin.dsoa.api.service.impl.ConstraintImpl;
import br.ufpe.cin.dsoa.api.service.impl.NonFunctionalSpecificationImpl;
import br.ufpe.cin.dsoa.api.service.impl.RequiredPortImpl;
import br.ufpe.cin.dsoa.api.service.impl.ServiceSpecification;
import br.ufpe.cin.dsoa.api.service.impl.ServiceSpecificationImpl;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentFactory;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentInstanceManager;
import br.ufpe.cin.dsoa.platform.component.DsoaConstraintParser;
import br.ufpe.cin.dsoa.util.Constants;

/**
 * The DsoaDependencyHandler is responsible for taking care of the service binding process, which is enacted by a dependency injection mechanism.
 * In a Dsoa application, the required services are represented by a collection of declared required fields, which specified
 * using dsoa:requires tag. Each required field is represented by an instance of the Dependency class, which is maintained in
 * a List<Dependency>. In DSOA, each one of those bindings is managed by a DependencyManager, which is an autonomic qos-aware biding
 * manager. Whenever a component instance is starting, this handler iterate over the dependency list and asks each dependency to start itself. 
 *
 * @author fabions
 */
public class DsoaRequiresHandler extends PrimitiveHandler {
	
	public static final String HANDLER_NAME = "br.ufpe.cin.dsoa:requires";
	
	/*
	 * A handler has access to the corresponding ComponentInstance (InstanceManager) via
	 * the inherited getInstanceManager() method.
	 */
	private boolean started;
	
	/**
	 * A list of Binding meta-objects, each one corresponding to a required service 
	 */
	private List<Binding> bindings = new ArrayList<Binding>();
	
	/**
	 * A description object intended to keep compatibility with iPojo expectations
	 */
	private DsoaRequiresHandlerDescription description;
	

	/**
	 * Initialize the ComponentFactory in order to help the building of the DsoaComponentType meta-object.
	 * This method identifies every required service and builds a corresponding RequiredPort meta-object.
	 * The RequiredPort meta-object is added to the DsoaComponentType meta-object in order to complete 
	 * its description.
	 * 
	 * @author fabions
	 */
	@Override
	public void initializeComponentFactory(org.apache.felix.ipojo.architecture.ComponentTypeDescription typeDesc, Element metadata) throws ConfigurationException {
		DsoaComponentFactory dsoaFactory = (DsoaComponentFactory)this.getFactory();
		DsoaComponentType dsoaComponentType = dsoaFactory.getComponentType();

		/* 
		 * The pojoMetadata is available since the Factory instantiation. Remember that
		 * the pojoMetadata represents ComponentType meta-data, not instance meta-data.
		 */
		PojoMetadata pojoMetadata = getFactory().getPojoMetadata();
		
		Element[] requiresTags = metadata.getElements(Constants.REQUIRES_TAG, Constants.REQUIRES_TAG_NAMESPACE);
		for (Element requiresTag : requiresTags) {
			String field = (String) requiresTag.getAttribute(Constants.REQUIRES_ATT_FIELD);
			List<ConstraintImpl> constraintList = DsoaConstraintParser.getConstraintList(requiresTag.getElements(Constants.CONSTRAINT_TAG));
			NonFunctionalSpecification nonFunctionalSpecification = new NonFunctionalSpecificationImpl(constraintList);
			
			FieldMetadata fieldMetadata = pojoMetadata.getField(field);
			
			String itfName = fieldMetadata.getFieldType();
			ServiceSpecification serviceSpecification =  new ServiceSpecificationImpl(itfName, nonFunctionalSpecification);
			RequiredPortImpl requiredPort = new RequiredPortImpl(field, serviceSpecification);
			dsoaComponentType.addRequiredPort(requiredPort);
		}
	};

	/**
	 * Configure the dependency handler. This method is called when the Handler is already attached to the ComponentInstance.
	 * 
	 * As part of the configuration task, this handler extracts the tags representing required services, 
	 * each one will correspond to an instance Binding that will be created and maintained by a DsoaBindingManager 
	 * (for a while refered to as DependencyManager).
	 * 
	 * A required service is represented by a RequiredPort meta-object that is specified by a requires tag 
	 * (br.ufpe.cin.dsoa:requires). This tag indicates a field attribute, which corresponds to the port name. The class of
	 * this field corresponds to the FunctionalInterface of the required service. Besides the field attribute,
	 * a requires tag has a collection of Constraints elements (see Constants.CONSTRAINT_TAG), which together define the port's NonFunctionalSpecification.
	 * As we see, the requires tag defines both, Functional and Non-functional specifications which are used to
	 * guide the service selection process. 
	 * 
	 * In this context, once that a service that match the specified requirements is selected, a corresponding Binding meta-object 
	 * is created which refers to the selected service. This Binding is encharged of creating a proxy object that is
	 * injected on the corresponding field. This proxy is used as an indirection mechanism to enable the binding management. 
	 * As we mentioned previously, this management task is performed by a DsoaBindingManager.
	 * 
	 * @param metadata the description of the component type. This metadata represents the component as a whole,
	 *  that is, it includes the whole component tag, not just the handler part.
	 * @param context the bundle context of the bundle containing the factory.
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void configure(Element metadata, Dictionary configuration) throws ConfigurationException {
		DsoaComponentInstanceManager manager = (DsoaComponentInstanceManager)this.getInstanceManager();
		DsoaComponentInstance componentInstance = manager.getDsoaComponentInstance();
		DsoaComponentType componentType = componentInstance.getComponentType();
		
		/*
		 * It is important to see that we can only create Bindings here
		 * since in the initializeComponentFactory only the ComponentType
		 * was available, not the ComponentInstance.
		 */
		PojoMetadata pojoMetadata = getFactory().getPojoMetadata();
		for(RequiredPort requiredPort : componentType.getRequiredPortList()) {
			FieldMetadata fieldMetadata = pojoMetadata.getField(requiredPort.getName());
			BindingImpl binding = new BindingImpl(this, componentInstance, requiredPort, buildProperties(configuration));
			this.register(fieldMetadata, binding);
			this.bindings.add(binding);
		}
		
		description = new DsoaRequiresHandlerDescription(this); 
	}
	
	@SuppressWarnings("rawtypes")
	protected List<br.ufpe.cin.dsoa.api.service.Property> buildProperties(Dictionary props) {
		List<br.ufpe.cin.dsoa.api.service.Property> propList = new ArrayList<br.ufpe.cin.dsoa.api.service.Property>();
        Enumeration keys = props.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            Object value = props.get(key);
            br.ufpe.cin.dsoa.api.service.Property prop;
            prop = new br.ufpe.cin.dsoa.api.service.impl.PropertyImpl(key, value, value.getClass().getName());
            propList.add(prop);
        }
        return propList;
    }
	
	
    /**
     * Handler start method.
     * @see org.apache.felix.ipojo.Handler#start()
     */
    public void start() {
        started = true;
        setValidity(false);
    	if (getDsoaPlatform() != null) {
	    	startDependencies();
    	}
    }
    
    /**
     * Handler stop method.
     * @see org.apache.felix.ipojo.Handler#stop()
     */
    public void stop() {
        this.stopDependencies();
        this.setValidity(false);
        started = false;
    }

	public void startDependencies() {
		synchronized (bindings) {
		    for (Binding binding : bindings) {
		        binding.start();
		    }
		}
		computeState();
	}
	
	public void stopDependencies() {
		synchronized (bindings) {
		    for (Binding binding : bindings) {
		        binding.stop();
		    }
		}
	}

    public void computeState() {
        if (!started) {
            return;
        }
        
        boolean initialState = getValidity();
        boolean valid = true;
        
        synchronized (bindings) {
            for (Binding binding: bindings) {
                if (!binding.isValid()) {
                    valid = false;
                    break;
                }
            }
            
            if (valid) {
                if (!initialState) {
                    setValidity(true);
                }
            } else {
                if (initialState) {
                    setValidity(false);
                }
            }

        }
    }

	public DsoaPlatform getDsoaPlatform() {
		return ((DsoaComponentInstanceManager)this.getInstanceManager()).getDsoaPlatform();
	}
    
	/**
	 * This method instructs the instance manager to call the dependency whenever the corresponding
	 * field is accessed (through onGet and onSet methods (see that the dependency implements FieldInterceptor interface)
	 * 
	 * @param fieldmeta
	 * @param dependency
	 */
	private void register(FieldMetadata fieldmeta, BindingImpl binding) {
		bindings.add(binding);
		getInstanceManager().register(fieldmeta, binding);
	}
	
	@Override
	public String toString() {
		return "DependencyHandler [bindings=" + bindings + "]";
	}

	@Override
	public HandlerDescription getDescription() {
		return this.description;
	}

	public List<Binding> getBindings() {
		return this.bindings;
	}
}
