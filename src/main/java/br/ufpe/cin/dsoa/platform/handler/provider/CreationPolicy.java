package br.ufpe.cin.dsoa.platform.handler.provider;

import org.apache.felix.ipojo.InstanceManager;

public interface CreationPolicy {
	
	public Object createInstance(InstanceManager m_manager);//FIXME:PROPERTIES

}
