package br.ufpe.cin.dsoa.api.service.impl;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.InstanceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.DsoaComponentInstance;
import br.ufpe.cin.dsoa.api.service.Port;
import br.ufpe.cin.dsoa.api.service.Property;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.platform.component.DsoaConstraintParser;
import br.ufpe.cin.dsoa.platform.handler.provider.DsoaProvidesHandler;

public class ServiceInstanceImpl extends PortInstanceImpl implements ServiceInstance, ServiceFactory {

	/**
	 * IPojo REGISTRED State
     */
    public static final int REGISTERED = 1;

    /**
     * IPojo UNREGISTRED State
     */
    public static final int UNREGISTERED = 0;
	
    /**
     * The Dsoa Provides Handler responsible for managing
     * service provisioning
     */
    private DsoaProvidesHandler handler;
    
	/**
	 * The corresponding DsoaComponentInstance meta-object
	 */
	private DsoaComponentInstance componentInstance;
	
	private ServiceRegistration serviceRegistration;
	
	/**
	 * The instance configuration properties
	 */
	@SuppressWarnings("rawtypes")
	private Dictionary configuration;
	
	/**
	 * The real service factory implementation
	 */
	private ServiceFactory factory;
	

	/**
	 * At this point, the configuration already contains the attributes that are part of the iPojo "instance tag" as well as
	 * the properties that are enclose between its start and end tags. 
	 * 
	 * @param handler
	 * @param componentInstance
	 * @param port
	 * @param configuration
	 */
	public ServiceInstanceImpl(DsoaProvidesHandler handler, DsoaComponentInstance componentInstance, Port port, Dictionary configuration) {
		super(componentInstance.getName()+"_"+port.getName(), port, buildProperties(configuration));
		this.componentInstance = componentInstance;
		this.handler = handler;
		this.factory = new ServiceFactoryImpl();
		this.configuration = configuration;
		this.componentInstance.addServiceInstance(this);
	}
	
	
	@SuppressWarnings("rawtypes")
	private static List<br.ufpe.cin.dsoa.api.service.Property> buildProperties(Dictionary props) {
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
     * Service object creation policy following the OSGi Service Factory
     * policy {@link ServiceFactory}.
     */
    private class ServiceFactoryImpl implements ServiceFactory {

        /**
         * OSGi Service Factory getService method.
         * Returns a new service object per asking bundle.
         * This object is then cached by the framework.
         * @param arg0 the bundle requiring the service
         * @param arg1 the service registration
         * @return the service object for the asking bundle
         * @see org.osgi.framework.ServiceFactory#getService(org.osgi.framework.Bundle, org.osgi.framework.ServiceRegistration)
         */
        public Object getService(Bundle arg0, ServiceRegistration arg1) {
            return handler.getInstanceManager().createPojoObject();
        }

        /**
         * OSGi Service Factory unget method.
         * Deletes the created object for the asking bundle.
         * @param arg0 the asking bundle
         * @param arg1 the service registration
         * @param arg2 the created service object returned for this bundle
         * @see org.osgi.framework.ServiceFactory#ungetService(org.osgi.framework.Bundle, org.osgi.framework.ServiceRegistration, java.lang.Object)
         */
        public void ungetService(Bundle arg0, ServiceRegistration arg1,
                Object arg2) {
            handler.getInstanceManager().deletePojoObject(arg2);
        }
    }	
	
	
	/**
	 * By now, the ServiceInstance name comes from the "service.pid" ou  "service.id"
	 * @param port
	 * @param props
	 * @param reference
	 */
	public ServiceInstanceImpl(Port port, List<Property> props, ServiceReference reference) {
		super(port.getName(), port, props);
	}

    /**
     * Get the service reference of the service registration.
     * @return the service reference of the provided service (null if the
     * service is not published).
     */
    public ServiceReference getServiceReference() {
        if (serviceRegistration == null) {
            return null;
        } else {
            return serviceRegistration.getReference();
        }
    }

    /**
     * Returns a service object for the dependency.
     * @see org.osgi.framework.ServiceFactory#getService(org.osgi.framework.Bundle, org.osgi.framework.ServiceRegistration)
     * @param bundle : the bundle
     * @param registration : the service registration of the registered service
     * @return a new service object or a already created service object (in the case of singleton) or <code>null</code>
     * if the instance is no more valid.
     */
    public Object getService(Bundle bundle, ServiceRegistration registration) {
        if (handler.getInstanceManager().getState() == InstanceManager.VALID) {
            return factory.getService(bundle, registration);
        } else {
            return null;
        }
    }

    /**
     * The unget method.
     *
     * @see org.osgi.framework.ServiceFactory#ungetService(org.osgi.framework.Bundle,
     * org.osgi.framework.ServiceRegistration, java.lang.Object)
     * @param bundle : bundle
     * @param registration : service registration
     * @param service : service object
     */
    public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
        factory.ungetService(bundle, registration, service);
    }

    /**
     * Registers the service. The service object must be able to serve this
     * service.
     * This method also notifies the creation strategy of the publication.
     */
    protected synchronized void registerService() {
        // Unregister if registered
        if (serviceRegistration != null) {
            unregisterService();
        }

        if (handler.getInstanceManager().getState() == ComponentInstance.VALID
                && serviceRegistration == null ) {
            // Build the service properties list

            BundleContext bc = handler.getInstanceManager().getContext();
            // Security check
            Properties serviceProperties = getRegisteredProperties();
            
            serviceRegistration = bc.registerService(getPublishedInterfaceNames(), this, serviceProperties);
        }
    }

    public String[] getPublishedInterfaceNames() {
    	return new String[] { getPort().getServiceSpecification().getFunctionalInterface().getInterfaceName() } ;
    }
    
    private Properties getRegisteredProperties() {
    	Properties publishedProps = new Properties();
    	addProperty("service.pid", this.getName(), String.class.getName());
    	this.configuration.put("service.pid", this.getName());
    	
    	// Add instance configuration data 
    	Enumeration propNames = configuration.keys();
    	while(propNames.hasMoreElements()) {
    		String propName = (String)propNames.nextElement();
    		publishedProps.put(propName, configuration.get(propName));
    	}
    	
    	// We shall not add constraints to our meta-model properties
    	for(Constraint constraint : this.getPort().getServiceSpecification().getNonFunctionalSpecification().getConstraints()) {
    		String metric = constraint.getAttributeId();
    		String operation = constraint.getOperation();
    		String expression = constraint.getExpression().getAlias();
    		Double doubleVal = new Double(constraint.getThreashold());
    		publishedProps.put(DsoaConstraintParser.getOsgiConstraintKey(metric, operation, expression), doubleVal);
    	}
    	
		return publishedProps;
	}

	/**
     * Unregisters the service.
     */
    protected synchronized void unregisterService() {
    	// Create a copy of the service reference in the case we need
    	// to inject it to the post-unregistration callback.

    	ServiceReference ref = null;
        if (serviceRegistration != null) {
    		ref = serviceRegistration.getReference();
            serviceRegistration.unregister();
            serviceRegistration = null;
        }

    }

    /**
     * Get the current provided service state.
     * @return The state of the provided service.
     */
    public int getState() {
        if (serviceRegistration == null) {
            return UNREGISTERED;
        } else {
            return REGISTERED;
        }
    }
    
}
