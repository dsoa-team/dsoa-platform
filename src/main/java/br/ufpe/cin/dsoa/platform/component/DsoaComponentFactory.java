package br.ufpe.cin.dsoa.platform.component;

import java.util.Dictionary;
import java.util.Set;

import org.apache.felix.ipojo.ComponentFactory;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.FactoryStateListener;
import org.apache.felix.ipojo.Handler;
import org.apache.felix.ipojo.HandlerManager;
import org.apache.felix.ipojo.IPojoContext;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.ServiceContext;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.architecture.ComponentTypeDescription;
import org.apache.felix.ipojo.composite.CompositeManager;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.util.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.api.service.DsoaComponentType;
import br.ufpe.cin.dsoa.api.service.impl.DsoaComponentTypeImpl;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;

/**
 * This class defines a factory responsible for the DsoaComponentType. When a
 * new component type is defined using an instance of the DsoaComponentType, it
 * creates a corresponding Meta-Object, which contains meta-data concerning the
 * defined type. This meta-data determines the class that has the business code
 * which the component shall execute and the services that the component
 * requires and provides. When an instance of this component type is created
 * (that is, a DsoaComponentInstance), that Meta-Object is used as a Template to
 * guide the component instantiation process.
 * 
 * The factory creation and initialization process:
 * 
 * 1. Identification of an extension Bundle: a new component type is defined
 * (through the manifest.mf header which is parsed).
 * 
 * 2. Factory instantiation: An instance of the factory is created, during which
 * it identifies the required handlers (just identification)
 * 
 * 3. Factory initialization (start): The method start is called in order to let
 * the factory verifies its state and build the internal representation of the
 * corresponding component type.
 * 
 * @author fabions
 * 
 */
public class DsoaComponentFactory extends ComponentFactory  {

	/*
	 * Inherited attributes: From IPojoFactory protected
	 * ComponentTypeDescription m_componentDesc: Describe provided interfaces
	 * (String[]) and publishedProperties. Give access to the factory. protected
	 * Element m_componentMetadata: hierarchical component description protected
	 * String m_factoryName: component classname (if factory name is not
	 * specified) protected Map<String,ComponentInstance> m_componentInstances:
	 * instance map protected BundleContext m_context: OSGi Context protected
	 * List<RequiredHandler> m_requiredHandlers: list containing
	 * RequiredHandlers protected List<FactoryStateListener> m_listeners:
	 * factory listeners protected boolean m_isPublic protected String m_version
	 * protected ServiceRegistration m_sr: the registry entry reffering to this
	 * factory protected int m_state: state of this factory From ComponetFactory
	 * protected Tracker m_tracker: used to track handler factories private
	 * String m_classname (via getClassname()) private PojoMetadata
	 * m_manipulation (via getPojoMetadata()) private FactoryClassLoader
	 * m_classLoader (via getBundleClassLoader()): this class defines the
	 * classloader attached to a factory. This class loader is used to load the
	 * implementation (e.g. manipulated) class.
	 */

	/**
	 * The Meta-object representing the DsoaComponentType
	 */
	private DsoaComponentType componentType;

	/**
	 * A tracker to verify the availability of the DsoaPlataform and its
	 * services.
	 */
	private ServiceTracker dsoaServiceTracker;

	/**
	 * The supporting DsoaPlatform
	 */
	private DsoaPlatform dsoa;

	/**
	 * The Constructor
	 * 
	 * @param context
	 *            : the OSGi bundle context where the factory is defined
	 * @param element
	 *            : the parsed Manifest description (used as a mechanism to
	 *            embed the component type definition into the platform
	 * @throws ConfigurationException
	 */
	public DsoaComponentFactory(BundleContext context, Element element)
			throws ConfigurationException {
		/*
		 * Initializes attributes such as m_componentMetadata (the iPojo
		 * internal representation of the component meta-data, that is an array
		 * of Elements), the m_factoryName, a m_logger, but, most importantly,
		 * calls @see ComponentFactory.getRequiredHandlerList() in order to
		 * determine the required components (this is delegated to the
		 * ComponentFactory class). This stage is where the architecture handler
		 * is included by default. After that, it calls method check, to have an
		 * opportunity to verify whether the component type definition is
		 * consistent. The element is now available as m_componentMetada.
		 */
		super(context, element);
	}

	/**
	 * Check the component description. Called during factory instantiation
	 * process.
	 */
	public void check(Element metadata) throws ConfigurationException {
		super.check(metadata);

		if (metadata.getAttribute(DsoaComponentType.NAME) == null) {
			throw new ConfigurationException("A component type needs a name : "
					+ metadata);
		}
	}

	/**
	 * During this stage (the begining of the factory starting cycle (@see
	 * org.apache.felix.ipojoiPojoFactory#start()), iPojo builds its internal
	 * component type representation. We use this hook to define our own
	 * component type, that is, a DsoaComponentType.
	 * 
	 * The starting process (iPojoFactory.start() workflow): 1. This method is
	 * called
	 * 
	 * 2. The ComponentFactory.starting() method: After this, it calls starting.
	 * At this point, iPojoComponentFactory opens a tracker to track the
	 * required Handlers. More specifically, it searches OSGi registry in order
	 * to discover Available Factories (that is, HandlerManagers) for the
	 * Handlers that it requires.
	 * 
	 * 3. The iPojoFactory.computeFactoryState() method: At this point,
	 * factories have an opportunity to verify whether everything it requires,
	 * mainly handlers, is available. Here, if the factory is valid (that is,
	 * the required handlers are available), the computeFactoryState calls
	 * iPojoFactory.computeDescription, which will give each required Handler an
	 * opportunity to contribute with the Factory, if it wants. This is done by
	 * calling PrimitiveHandler.initializeComponentFactory(m_componentDesc,
	 * m_componentMetadata) on each required handler. Remember that, up to this
	 * point, the handlers are not attached to a ComponentInstance and will be
	 * disposed after this call.
	 * 
	 * In our case, our handlers will help the factory to build the
	 * DsoaCompontType Meta-Object by including meta-data concerning the
	 * required and provided services represented by the corresponding ports.
	 * So, at this posterior state, the DsoaComponentType definition, which is
	 * firstly build here, will be continued by the introduction of the
	 * Handler's contribution.
	 * 
	 * 4. The iPojoFactory.computeDescription method: 
	 * This method is responsible for building the component description. 
	 * It is called by the computeFactoryState method when the Factory becomes valid. 
	 * We do nothing here as our Handlersare already called by the default implementation.
	 * 
	 * 5. Still as part of the iPojoFactory.computeFactoryState, after the
	 * description is built, the factory is published as a service, so that it
	 * can be used to create instances.
	 */
	@Override
	public ComponentTypeDescription getComponentTypeDescription() {
		this.componentType = new DsoaComponentTypeImpl(
				this.m_componentMetadata.getAttribute(DsoaComponentType.NAME),
				this.m_componentMetadata
						.getAttribute(DsoaComponentType.CLASSNAME));
		// Called to let iPojo build its internal description
		return super.getComponentTypeDescription();
	};

	/**
	 * Opens the tracker to the DsoaPlatform services. Vide the description
	 * (javadoc) of the getComponentTypeDescription method above.
	 */
	@Override
	public void starting() {
		if (dsoaServiceTracker != null) {
			return; // Already started the Dsoa tracker
		} else {
			this.dsoaServiceTracker = new ServiceTracker(m_context, DsoaPlatform.class.getName(), new DsoaTrackerCustomizer());
			this.dsoaServiceTracker.open();
		}
		super.starting();
	};

	/**
	 * Verifies whether the DsoaPlatform and calls its superclass equivalent
	 * method to verify the state and get the contributions of the handlers to
	 * building the Component Type.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected void computeFactoryState() {
		if (this.dsoa != null) {
			super.computeFactoryState();
		} else {
			if (m_state == VALID) {
				m_state = INVALID;

				// Notify listeners.
				for (int i = 0; i < m_listeners.size(); i++) {
					((FactoryStateListener) m_listeners.get(i)).stateChanged(
							this, INVALID);
				}

				// Dispose created instances.
				Set col = m_componentInstances.keySet();
				String[] keys = (String[]) col.toArray(new String[col.size()]);
				for (int i = 0; i < keys.length; i++) {
					ComponentInstance instance = (ComponentInstance) m_componentInstances
							.get(keys[i]);
					if (instance.getState() != ComponentInstance.DISPOSED) {
						instance.dispose();
					}
					INSTANCE_NAME.remove(instance.getInstanceName());
				}

				m_componentInstances.clear();

				if (m_sr != null) {
					m_sr.setProperties(m_componentDesc.getPropertiesToPublish());
				}

				return;
			}
		}
	};

	/**
	 * This method is called by calling the computeFactoryState on the super class (as we see above)
	 * 
	 * Computes the component type description. To do this, it creates a 'ghost'
	 * instance of the handler and calls the
	 * {@link Handler#initializeComponentFactory(ComponentTypeDescription, Element)}
	 * method. The handler instance is then deleted. The factory must be valid
	 * when calling this method. This method is called with the lock.
	 */
	protected void computeDescription() {
		// Just call super.
		// We decided to represent just to remember that it is part of the factory starting process
		// SO, BEFORE INSTANCE CREATION.
		super.computeDescription();
	}

	/**
	 * Create an instance from the current factory. 
	 * 
	 * In fact, the entry point for ComponentInstance creation in iPojo platform is the method 
	 * @see org.apache.felix.ipojo.IPojoFactory#createComponentInstance(Dictionary configuration). 
	 * 
	 * This method calls @see org.apache.felix.ipojo.IPojoFactory#createComponentInstance(Dictionary configuration, ServiceContext serviceContext),
	 * which modifies the configuration in order to include the "instance.name" and tries to get
	 * a HandlerManager corresponding to each required handler by calling createComponentInstance(null, context) on the
	 * handler factory (HandlerManagerFactory). This will start a recursive process where it individual HandlerManager will be  
	 * created. 
	 * 
	 * A HandlerManagerFactory class extends ComponentFactory, but does not override the createInstance(,) method. 
	 * (this is to allow the creation to follow up, so that each handler can create instances of the handlers that itself requires, that
	 * is, the recursive processes that we mentioned above). This process will eventually lead to the following
	 * calling on the HandlerManagerFactory class: @see org.apache.felix.ipojo.createInstance(Dictionary configuration, IPojoContext context, HandlerManager[] handlers)
	 * to effectively create the HandlerManagers (managers responsible for the Handlers).  
	 * 
	 * When the creation method mentioned above is called on the HandlerManagerFactory, an instance of
	 * HandlerManager (a manager responsible for the handler, equivalent to the InstanceManager to the component instances) is created
	 * by calling: HandlerManager(this, context, handlers). This constructor calls the constructor of the InstanceManager class (superclass).
	 * Each InstanceManager, and consequently HandlerManager, has a PrimitiveInstanceDescription that it creates on the constructor.
	 * This description listens for altering in the state of the corresponding instance, by registering itself with the instance
	 * through the addInstanceStateListener method. So, every time that the instance state changes, the description is notified.
	 * 
	 * Once that a HandlerManager is created, its configure method is called. Not be confused with the Handler.configure.
	 * This calling leads to the configure call on the InstanceManager, since the HandlerManager extends that class. 
	 * This calling, then, calls the init on each required handler (via HandlerManager). This init (vide HandlerManager) calls:
	 * 
	  	createHandlerObject();
        m_handler.setFactory(instance.getFactory());
        m_handler.attach(instance);
        m_handler.configure(metadata, configuration);
        
        So it goes in each Handler class and calls its setFactory, attach and configure methods! Here is that our handler code is called.
        
        In summary, when a component instance is created, each required handler manager is created and configured. This process is recursive.
        During the handler manager configuration process, an instance of the real Handler is created, 
        attached to the component instance, and has its configure method called.
	 * 
	 * This sequence will eventually lead to the execution
	 * of the method bellow, which is overridden here. Basically, this method is
	 * responsible for creating, configuring and starting a ComponentInstance
	 * (InstanceManager) as we clearly see here.
	 * 
	 * @param configuration
	 *            : instance configuration
	 * @param context
	 *            : bundle context to inject in the instance manager
	 * @param handlers
	 *            : array of handler object to attached on the instance
	 * @return the created instance
	 * @throws ConfigurationException
	 *             either the instance configuration or the instance starting
	 *             has failed
	 * @see org.apache.felix.ipojo.ComponentFactory#createInstance(java.util.Dictionary,
	 *      org.apache.felix.ipojo.IPojoContext,
	 *      org.apache.felix.ipojo.HandlerManager[])
	 */
	@SuppressWarnings("rawtypes")
	public DsoaComponentInstanceManager createInstance(
			Dictionary configuration, IPojoContext context,
			HandlerManager[] handlers) throws ConfigurationException {
		DsoaComponentInstanceManager inst = new DsoaComponentInstanceManager(
				this, context, handlers);
		/*
		 * The instance configuration process involves initializing every
		 * HandlerManager (the instance manager responsible for managing a
		 * Handler. During HandlerManager initialization, an instance of the
		 * Handler is created, it has a corresponding Factory set (the
		 * ComponentFactory associated to the ComponentInstance), it is attached
		 * to the ComponentInstance via attach method call, and finally, the
		 * handler is configured, via a configure call. In summary, the instance
		 * configuration process comprises the creation and initialization of
		 * every required Handler.
		 * 
		 */
		inst.configure(m_componentMetadata, configuration);
		
		/* 
		 * A configured instance has already its handlers created,
		 * configured and attached to itself. So now it can be started.
		 * During the instance starting process, the Handlers' descriptions
		 * are got and included in the instance description itself (see 
		 * the start method).
		 * 
		 * After that, each configured handler is started, as we see bellow:
		 * 
		 * for (int i = 0; i < m_handlers.length; i++) {
	            m_handlers[i].addInstanceStateListener(this);
	            try {
	                m_handlers[i].start();
	            } catch (IllegalStateException e) {
	                m_logger.log(Logger.ERROR, e.getMessage());
	                stop();
	                throw e;
	            }
        	}
        	
        	Observe that, as we DO NOT override the start method on our DsoaComponentInstance class,
        	it inherit this start, so it will start corresponding handlers and also register with
        	their HandlerManagers in order to propagate status information. The start calling on the HandlerManager 
        	will be propaged to the corresponding Handler.
        	
        	Finally, the instance starting process involves verifying the instance status. As we see bellow,
        	this process is done by asking each required handler what is its status. 
        	
        	 for (int i = 0; i < m_handlers.length; i++) {
	            if (m_handlers[i].getState() != VALID) {
	                setState(INVALID);
	                return;
	            }
	        }
	        setState(VALID);
	        
	        In summary, the instance will only be valid if the required handlers are valid.
	        
		 */
		inst.start();
		return inst;
	}

	/**
	 * Reconfigure an existing instance.
	 * 
	 * @param properties
	 *            : the new configuration to push.
	 * @throws UnacceptableConfiguration
	 *             : occurs if the new configuration is not consistent with the
	 *             component type.
	 * @throws MissingHandlerException
	 *             : occurs when an handler is unavailable when creating the
	 *             instance.
	 * @see org.apache.felix.ipojo.Factory#reconfigure(java.util.Dictionary)
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void reconfigure(Dictionary properties)
			throws UnacceptableConfiguration, MissingHandlerException {
		if (properties == null || properties.get("name") == null) {
			throw new UnacceptableConfiguration(
					"The configuration does not contains the \"name\" property");
		}
		String name = (String) properties.get("name");

		ComponentInstance instance = (CompositeManager) m_componentInstances
				.get(name);

		if (instance == null) {
			return; // The instance does not exist.
		}

		instance.reconfigure(properties); // re-configure the component
	}

	public DsoaPlatform getDsoaPlatform() {
		return this.dsoa;
	}
	
	public DsoaComponentType getComponentType() {
		return this.componentType;
	}

	public void superComputeFactoryState() {
		super.computeFactoryState();
	}
	
	/*
	 * DSOA TRACKER CUSTOMIZED METHODS:
	 */
	class DsoaTrackerCustomizer implements ServiceTrackerCustomizer {
		public Object addingService(ServiceReference reference) {
			dsoa = (DsoaPlatform) m_context.getService(reference);
			if (m_state == INVALID) {
				superComputeFactoryState();
			}
			return dsoa;
		}
		
		public void modifiedService(ServiceReference reference, Object service) {
			// Just do nothing!
		}
		
		public void removedService(ServiceReference reference, Object service) {
			dsoa = null;
			m_context.ungetService(reference);
			m_state = INVALID;
		}	
	}


}
