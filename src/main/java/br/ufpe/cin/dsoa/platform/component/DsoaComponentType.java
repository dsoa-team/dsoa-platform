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

public class DsoaComponentType extends ComponentFactory {

	/**
	 * The component type name.
	 */
	private String m_name;

	public DsoaComponentType(BundleContext context, Element element)
			throws ConfigurationException {
		super(context, element);
	}

	/**
	 * Check the component description
	 */
	public void check(Element metadata) throws ConfigurationException {
		super.check(metadata);

		m_name = metadata.getAttribute("name");
		if (m_name == null) {
			throw new ConfigurationException("A component type needs a name : "
					+ metadata);
		}
	}
	
    /**
     * Create an instance from the current factory.
     * @param configuration : instance configuration
     * @param context : bundle context to inject in the instance manager
     * @param handlers : array of handler object to attached on the instance 
     * @return the created instance
     * @throws ConfigurationException either the instance configuration or the instance starting has failed 
     * @see org.apache.felix.ipojo.ComponentFactory#createInstance(java.util.Dictionary, org.apache.felix.ipojo.IPojoContext, org.apache.felix.ipojo.HandlerManager[])
     */
    public DsoaComponentInstance createInstance(Dictionary configuration, IPojoContext context, HandlerManager[] handlers) throws ConfigurationException {
    	DsoaComponentInstance inst = new DsoaComponentInstance(this, context, handlers);
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

}
