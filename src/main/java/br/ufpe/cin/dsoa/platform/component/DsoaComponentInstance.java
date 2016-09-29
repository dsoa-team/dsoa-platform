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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.ipojo.ComponentFactory;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.HandlerManager;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.InstanceStateListener;
import org.apache.felix.ipojo.PrimitiveInstanceDescription;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.BundleContext;

/**
 * iPOJO Composite manager. The composite manager class manages one instance of
 * a component type which is a composition. It manages component lifecycle, and
 * handlers...
 * 
 * @author <a href="mailto:dev@felix.apache.org">Felix Project Team</a>
 */
public class DsoaComponentInstance extends InstanceManager implements ComponentInstance, InstanceStateListener {

    private DsoaComponentInstanceDescription m_description;


	/**
     * Construct a new Component Manager.
     * @param factory : the factory managing the instance manager
     * @param context : the bundle context to give to the instance
     * @param handlers : the handlers to plug
     */
    public DsoaComponentInstance(DsoaComponentType factory, BundleContext context, HandlerManager[] handlers) {
    	super(factory, context, handlers);
    	m_description = new DsoaComponentInstanceDescription(factory.getComponentDescription(), this);
    }


	@Override
	public InstanceDescription getInstanceDescription() {
		return m_description;
	}
}

