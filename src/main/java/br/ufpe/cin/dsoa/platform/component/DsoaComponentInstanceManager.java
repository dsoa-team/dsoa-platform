package br.ufpe.cin.dsoa.platform.component;

/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.felix.ipojo.HandlerManager;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.InstanceStateListener;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.BundleContext;

import br.ufpe.cin.dsoa.api.service.impl.ComponentInstanceImpl;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.component.autonomic.DsoaComponentInstanceAutonomicManager;


/**
 * This class represents a DsoaComponentInstanceManager, referred here as dsoa-manager. A dsoa-manager manages a ComponentInstance.  
 * In iPojo, a component instance can be created using the instance tag. An important point concerning the way that iPojo handles
 * instances is that the instance meta-data (embedded in the tag) is parsed into a Dictionary element containing a collection of
 * properties which are used to configure the instance. This is different from the way that the component meta-data is dealt with.
 * In fact, the component meta-data is parsed into an Element object which represents the component type configuration. The code bellow
 * illustrates the configuration building process (parsing):
 * 
 		String name = instance.getAttribute("name");
        String comp = instance.getAttribute("component");
        String version = instance.getAttribute("version");

        if (name != null) {
            dict.put("instance.name", instance.getAttribute("name"));
        }

        if (comp == null) {
            throw new ParseException("An instance does not have the 'component' attribute");
        }

        dict.put("component", comp);

        if (version != null) {
            dict.put("factory.version", version);
        }

        Element[] props = instance.getElements("property");

        for (int i = 0; props != null && i < props.length; i++) {
            parseProperty(props[i], dict);
        }
        
 * As we can see from the parsing process, the name attribute is translated into a "instance.name" property,
 * the component attribute is translated to a "component" property and the "version" attribute is translated
 * into the "factory.version" property.
 * 
 * IPojo framework has an Extender component which inspect started bundles looking for iPojo markers in their MANIFESTs. 
 * When as instance tag is found, iPojo searches for a corresponding ComponentFactory, which has been previously deployed, 
 * (in this particular case, a DsoaComponentFactory) and calls its createInstance method. This calling will eventually
 * lead to the creation of a DsoaComponentInstanceManager which will manage the component itself. The management activities include
 * the component life-cycle management, the binding of the required services (via a dependency injection mechanism), and the publication of the
 * provided services.
 * 
 * In order to make a ComponentInstance available, the corresponding ComponentFactory must configure it after its creation. The factory
 * does this by calling the configure method (@see InstanceManager#configure(org.apache.felix.ipojo.metadata.Element, java.util.Dictionary).
 * In our case, we DON'T OVERRIDE the configure method, since its standard behavior is adequate. This standard behavior involves the INITIALIZATION
 * of every required HandlerManager, when the Handler instances are created, receive the component factory (via setFactory method), are attached
 * to the ComponentInstance (via attach method), and are configured (via configure method, which is overriden in virtually every Handler).   
 * 
 * @author fabions
 */
public class DsoaComponentInstanceManager extends InstanceManager implements org.apache.felix.ipojo.ComponentInstance, InstanceStateListener {

	/**
	 * Just to be compatible with iPojo expectations
	 */
	private DsoaComponentInstanceDescription m_description;
 
    /**
     * Enable direct access to the ComponentFactory without casts which would be required 
     * if @see org.apache.felix.ipojo.InstanceManager#getFactory() method was used.
     */
    private DsoaComponentFactory factory;
    /**
     * A meta-object encapsulating component instance meta-data.
     */
    private ComponentInstanceImpl instance;
    
    /*
     * Inherited Attributes:
     * 
     * 	From InstanceManager:
      		protected String m_name;
		    protected String m_className;
		    protected final HandlerManager[] m_handlers;
		    protected int m_state = STOPPED;
		    protected List m_listeners = null;
		    protected List m_pojoObjects;
		    private final ComponentFactory m_factory;
     */
    
    
	/**
     * Construct a new DsoaComponentInstanceManager 
     * @param factory : the factory managing the instance manager
     * @param context : the bundle context to give to the instance
     * @param handlers : the handlers to plug
     */
    public DsoaComponentInstanceManager(DsoaComponentFactory factory, BundleContext context, HandlerManager[] handlers) {
    	super(factory, context, handlers);
    	this.factory = factory;
    	this.m_description = new DsoaComponentInstanceDescription(factory.getComponentDescription(), this);
    }


    @Override
    @SuppressWarnings("rawtypes")
    public void configure(Element metadata, java.util.Dictionary configuration) throws org.apache.felix.ipojo.ConfigurationException {
    	/* 
    	 * The instance creation must occur before the superclass delegation in order
    	 * to guarantee that the handlers can have access to the ComponentInstance during
    	 * their configuration. It is important to observe that m_name contains the component
    	 * instance name, that is configured using the "instance.name" property, which,
    	 * as mentioned above comes from the "name" attribute (vide comments above). This
    	 * is the reason that the ComponentInstance is instantiated just here, not in the
    	 * constructor, since there we have no access to the configuration data.
    	 */
    	this.instance = new ComponentInstanceImpl((String) configuration.get("instance.name"), this.factory.getComponentType(), 
    			this, new DsoaComponentInstanceAutonomicManager(getDsoaPlatform(), instance));
    	
    	/*
    	 * The following code should not be required since it is part
    	 * of the starting process, as we can see in InstanceManager.start() ln. 332.
    	 */
    	
/*    	Handler[] handlers = this.getRegistredHandlers();
    	for (Handler handler : handlers) {
    		handler.getHandlerManager().addInstanceStateListener(this);
    	}*/
    	
    	super.configure(metadata, configuration);
    } 
    
	public ComponentInstanceImpl getDsoaComponentInstance() {
		return this.instance;
	}

    
    public DsoaPlatform getDsoaPlatform() {
    	return this.factory.getDsoaPlatform();
    }
}

