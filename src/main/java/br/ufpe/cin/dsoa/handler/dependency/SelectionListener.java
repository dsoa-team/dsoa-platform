package br.ufpe.cin.dsoa.handler.dependency;

import org.osgi.framework.ServiceReference;

public interface SelectionListener {

	public void notifySelection(ServiceReference service);

}
