package br.ufpe.cin.dsoa.platform.handler.requires;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;

import br.ufpe.cin.dsoa.api.service.Binding;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.NonFunctionalSpecification;
import br.ufpe.cin.dsoa.api.service.RequiredPort;
import br.ufpe.cin.dsoa.api.service.ServiceSpecification;
import br.ufpe.cin.dsoa.api.service.impl.BindingImpl;
import br.ufpe.cin.dsoa.api.service.impl.ComponentInstanceImpl;
import br.ufpe.cin.dsoa.api.service.impl.ComponentTypeImpl;
import br.ufpe.cin.dsoa.api.service.impl.NonFunctionalSpecificationImpl;
import br.ufpe.cin.dsoa.api.service.impl.RequiredPortImpl;
import br.ufpe.cin.dsoa.api.service.impl.ServiceSpecificationImpl;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentFactory;
import br.ufpe.cin.dsoa.platform.component.DsoaComponentInstanceManager;
import br.ufpe.cin.dsoa.platform.component.DsoaConstraintParser;
import br.ufpe.cin.dsoa.util.Constants;

/**
 * The DsoaDependencyHandler is responsible for taking care of the service binding process, which is enacted by a 
 * dependency injection mechanism. This handler is a core element on the DsoaPlatform, and should only be used in
 * applications that use the ComponentType.
 * 
 * In a Dsoa application, the required services are represented by a collection of declared required fields, which specified
 * using dsoa:requires tag. Each required field is represented by an instance of the Dependency class, which is maintained in
 * a List<Dependency>. In DSOA, each one of those bindings is managed by a DependencyManager, which is an autonomic qos-aware biding
 * manager. Whenever a component instance is starting, this handler iterate over the dependency list and asks each dependency
 * to start itself. 
 *
 * @author fabions
 */
public class DsoaRequiresHandler extends PrimitiveHandler  {
	
	public static final String HANDLER_NAME = "br.ufpe.cin.dsoa:requires";
	
	
	/**
	 * A list of Binding meta-objects, each one corresponding to a required service 
	 */
	private List<Binding> bindings = new ArrayList<Binding>();
	
	
	/**
	 * Initialize the ComponentFactory in order to help the building of the ComponentType meta-object.
	 * This method is only called when the factory is when the factory becomes valid for the first time!
	 * It is called without an attached instance, so we can not depend on instances here!
	 * This method identifies every required service and builds a corresponding RequiredPort meta-object.
	 * The RequiredPort meta-object is added to the ComponentType meta-object in order to complete 
	 * its description.
	 * 
	 * @author fabions
	 */
	@Override
	public void initializeComponentFactory(org.apache.felix.ipojo.architecture.ComponentTypeDescription typeDesc, Element metadata) throws ConfigurationException {
		
		/* 
		 * The pojoMetadata is available since the Factory instantiation. Remember that
		 * the pojoMetadata represents ComponentType meta-data, not instance meta-data.
		 */
		DsoaComponentFactory dsoaFactory = (DsoaComponentFactory)this.getFactory();
		ComponentTypeImpl dsoaComponentType = dsoaFactory.getComponentType();
		PojoMetadata pojoMetadata = getFactory().getPojoMetadata();
		
		Element[] requiresTags = metadata.getElements(Constants.REQUIRES_TAG, Constants.REQUIRES_TAG_NAMESPACE);
		for (Element requiresTag : requiresTags) {
			String field = (String) requiresTag.getAttribute(Constants.REQUIRES_ATT_FIELD);
			List<Constraint> constraintList = DsoaConstraintParser.getConstraintList(requiresTag.getElements(Constants.CONSTRAINT_TAG),null);
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
	 * THIS METHOD IS CALLED WITH THE INSTANCE ALREADY CREATED AND ATTACHED BUT NOT STARTED!!! STARTING PROCESS COME NEXT!
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
		ComponentInstanceImpl componentInstance = manager.getDsoaComponentInstance();
		ComponentTypeImpl componentType = componentInstance.getComponentType();
		
		PojoMetadata pojoMetadata = getFactory().getPojoMetadata();
		for(RequiredPort requiredPort : componentType.getRequiredPortList()) {
			FieldMetadata fieldMetadata = pojoMetadata.getField(requiredPort.getName());
			//TODO 
			BindingImpl binding = new BindingImpl(this, componentInstance, requiredPort, buildProperties(configuration));
			this.register(fieldMetadata, binding);
		}
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
	
	
 
    
	public DsoaPlatform getDsoaPlatform() {
		DsoaComponentInstanceManager manager = (DsoaComponentInstanceManager)this.getInstanceManager();
		return manager.getDsoaPlatform();
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
		
		return new DsoaRequiresHandlerDescription(this);
	}

	public List<Binding> getBindings() {
		return this.bindings;
	}
	
	
	 class DsoaRequiresHandlerDescription extends HandlerDescription {

			private static final String STATUS_ELEMENT = "state";

			public DsoaRequiresHandlerDescription(DsoaRequiresHandler depHandler) {
				super(depHandler);
			}
			
			public Element getHandlerInfo() {
				Element descInfo = new Element("Handler", "");
		        String state = "valid";
		        for (Binding binding : bindings) {
		        	DsoaBindingDescription bd = new DsoaBindingDescription(binding);
		            if (!binding.isValid()) {
		                state = "invalid";
		            }
		            descInfo.addElement(bd.getInfo());
		        }
		        descInfo.addAttribute(new Attribute(STATUS_ELEMENT, state));
		        descInfo.addAttribute(new Attribute("name","br.ufpe.cin.dsoa:requires"));
		        return descInfo;
		        
		    }

	}
	 
	 /*
	  * OS MÉTODOS ABAIXO FORAM COPIADOS DA VERSÃO 1.0 DA PLATAFORMA PARA SEREM AVALIADOS
	  */
	 
		
	    /**
	     * Handler start method.
	     * @see org.apache.felix.ipojo.Handler#start()
	     */
	    public void start() {
	        setValidity(false);
		    for (Binding binding : bindings) {
		    	binding.start();
		    }
			computeState();
	    }
	    
	    public void computeState() {
	        synchronized (bindings) {
		        boolean initialState = getValidity();
		        boolean valid = true;
		        
	            for (Binding binding : bindings) {
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
	    
	    /**
	     * Handler stop method.
	     * @see org.apache.felix.ipojo.Handler#stop()
	     */
	    public void stop() {
	        this.stopDependencies();
	        this.setValidity(false);
	    }

		private void stopDependencies() {
			synchronized (bindings) {
			    for (Binding binding : bindings) {
			        binding.stop();
			    }
			}
		}

	    
	
}
