package br.ufpe.cin.dsoa.util;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.util.DsoaConstants;

public class Util {

	public static boolean isRemote(ServiceReference reference) {
		return reference.getProperty(DsoaConstants.REMOTE_SERVICE) != null;
	}

	public static boolean isProxy(ServiceReference reference) {
		return reference.getProperty(DsoaConstants.SERVICE_PROXY) != null;
	}
	
	public static String getId(ServiceReference reference) {
		return reference.getProperty(Constants.SERVICE_ID).toString();
	}
	
	public static String getPid(ServiceReference reference) {
		Object pid = reference.getProperty(Constants.SERVICE_PID);
		return pid == null ? null : pid.toString();
	}
}
