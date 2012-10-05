package br.ufpe.cin.dsoa.handlers.dependency;

import org.osgi.framework.ServiceReference;

public interface DependencyListener {

	public void setSelected(ServiceReference serviceDescription);

}
