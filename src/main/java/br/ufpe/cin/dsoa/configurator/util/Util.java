package br.ufpe.cin.dsoa.configurator.util;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.util.DsoaConstants;

public class Util {

	public static boolean isRemote(ServiceReference reference) {
		return reference.getProperty(DsoaConstants.REMOTE_SERVICE) != null;
	}

	public static boolean isProxy(ServiceReference reference) {
		return reference.getProperty(DsoaConstants.SERVICE_PROXY) != null;
	}
	
}
