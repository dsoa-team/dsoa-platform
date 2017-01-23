package br.ufpe.cin.dsoa.platform.component;

import java.util.Dictionary;

import org.apache.felix.ipojo.ComponentFactory;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.HandlerManager;
import org.apache.felix.ipojo.IPojoContext;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.composite.CompositeManager;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.api.service.DsoaComponentType;
import br.ufpe.cin.dsoa.api.service.impl.DsoaComponentTypeImpl;


public class DsoaComponentFactory extends ComponentFactory {
	
	/* Inherited attributes:
	 * 	From IPojoFactory
	 * 		protected ComponentTypeDescription m_componentDesc: Describe provided interfaces (String[]) and publishedProperties. Give access to the factory.
	 * 		protected Element m_componentMetadata: hierarchical component description
	 * 		protected String m_factoryName: component classname (if factory name is not specified)
	 * 		protected Map<String,ComponentInstance> m_componentInstances: instance map
	 * 		protected BundleContext m_context: OSGi Context
	 * 		protected List<RequiredHandler> m_requiredHandlers: list containing RequiredHandlers
	 * 		protected List<FactoryStateListener> m_listeners: factory listeners
	 * 		protected boolean m_isPublic
	 * 		protected String m_version
	 * 		protected ServiceRegistration m_sr: the registry entry reffering to this factory
	 * 		protected int m_state: state of this factory
	 * 	From ComponetFactory
	 * 		protected Tracker m_tracker: used to track handler factories
	 * 		private String m_classname (via getClassname())
	 * 		private PojoMetadata m_manipulation (via getPojoMetadata())
	 * 		private FactoryClassLoader m_classLoader (via getBundleClassLoader()):  this class defines the classloader attached to a factory.
     * 			This class loader is used to load the implementation (e.g. manipulated) class.
	 * 		
	 */

	private DsoaComponentType componentType;
	
	public DsoaComponentFactory(BundleContext context, Element element)
			throws ConfigurationException {
		super(context, element);
		this.componentType = new DsoaComponentTypeImpl(element.getAttribute(DsoaComponentType.NAME), element.getAttribute(DsoaComponentType.CLASSNAME));
	}

	/**
	 * Check the component description
	 */
	public void check(Element metadata) throws ConfigurationException {
		super.check(metadata);

		if (metadata.getAttribute(DsoaComponentType.NAME) == null) {
			throw new ConfigurationException("A component type needs a name : "
					+ metadata);
		}
	}
	
    /**
     * Create an instance from the current factory.
     * In fact, the entry point for ComponentInstance creation in iPojo platform 
     * is the method @see org.apache.felix.ipojo.IPojoFactory#createComponentInstance(Dictionary configuration).
     * This initial call will eventually lead to the execution of the method bellow, which is overridden here.
     * Basically, this method is responsible for creating, configuring and starting a ComponentInstance (InstanceManager)
     * as we clearly see here. 
     * 
     * @param configuration : instance configuration
     * @param context : bundle context to inject in the instance manager
     * @param handlers : array of handler object to attached on the instance 
     * @return the created instance
     * @throws ConfigurationException either the instance configuration or the instance starting has failed 
     * @see org.apache.felix.ipojo.ComponentFactory#createInstance(java.util.Dictionary, org.apache.felix.ipojo.IPojoContext, org.apache.felix.ipojo.HandlerManager[])
     */
	@SuppressWarnings("rawtypes")
    public DsoaComponentInstanceManager createInstance(Dictionary configuration, IPojoContext context, HandlerManager[] handlers) throws ConfigurationException {
    	DsoaComponentInstanceManager inst = new DsoaComponentInstanceManager(this, context, handlers);
    	/*
    	 * The instance configuration process involves initializing every HandlerManager (the instance manager responsible for managing 
    	 * a Handler. During HandlerManager initialization, an instance of the Handler is created, it has a corresponding Factory set
    	 * (the ComponentFactory associated to the ComponentInstance), it is attached to the ComponentInstance via attach
    	 * method call, and finally, the handler is configured, via a configure call. In summary, the instance configuration process
    	 * comprises the creation and initialization of every required Handler.
    	 * 
    	 * */
        inst.configure(m_componentMetadata, configuration);
        inst.start();
        return inst;
    }

	
    /**
     * Reconfigure an existing instance.
     * @param properties : the new configuration to push.
     * @throws UnacceptableConfiguration : occurs if the new configuration is
     * not consistent with the component type.
     * @throws MissingHandlerException : occurs when an handler is unavailable when creating the instance.
     * @see org.apache.felix.ipojo.Factory#reconfigure(java.util.Dictionary)
     */
	@SuppressWarnings("rawtypes")
    public synchronized void reconfigure(Dictionary properties) throws UnacceptableConfiguration, MissingHandlerException {
        if (properties == null || properties.get("name") == null) {
            throw new UnacceptableConfiguration("The configuration does not contains the \"name\" property");
        }
        String name = (String) properties.get("name");
        
        ComponentInstance instance = (CompositeManager) m_componentInstances.get(name);
        
        if (instance == null) {
            return; // The instance does not exist.
        }
        
        instance.reconfigure(properties); // re-configure the component
    }
    
    public DsoaComponentType getComponentType() {
    	return this.componentType;
    }

}
