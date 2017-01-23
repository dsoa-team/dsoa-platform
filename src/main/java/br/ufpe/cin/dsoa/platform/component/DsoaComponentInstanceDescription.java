package br.ufpe.cin.dsoa.platform.component;

import org.apache.felix.ipojo.architecture.ComponentTypeDescription;
import org.apache.felix.ipojo.architecture.InstanceDescription;



/**
 * Composite Instance Description.
 * 
 * @author <a href="mailto:dev@felix.apache.org">Felix Project Team</a>
 */
public class DsoaComponentInstanceDescription extends InstanceDescription {
    
    /**
     * Creates a Primitive Instance Description.
     * @param type the component type description
     * @param instance the instance description
     */
    public DsoaComponentInstanceDescription(ComponentTypeDescription type, DsoaComponentInstanceManager instance) {
        super(type, instance);
    }

}

