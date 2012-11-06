package br.ufpe.cin.dsoa.broker;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.Slo;
import br.ufpe.cin.dsoa.handlers.dependency.ServiceListener;


/**
 * 
 * @author David
 **/

public interface Broker {
	
	public void getBestService(String spe, List<Slo> slos, ServiceListener dep, List<ServiceReference> trash);
	//Tinha um mode;
	
}