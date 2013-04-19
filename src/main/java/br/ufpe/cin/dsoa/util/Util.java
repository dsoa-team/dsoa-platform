package br.ufpe.cin.dsoa.util;

import org.osgi.framework.ServiceReference;

public class Util {

	public static boolean isRemote(ServiceReference reference) {
		return reference.getProperty(Constants.REMOTE_SERVICE) != null;
	}

	public static boolean isProxy(ServiceReference reference) {
		return reference.getProperty(Constants.SERVICE_PROXY) != null;
	}
	
	public static String getId(ServiceReference reference) {
		return reference.getProperty(org.osgi.framework.Constants.SERVICE_ID).toString();
	}
	
	public static String getPid(ServiceReference reference) {
		Object pid = reference.getProperty(org.osgi.framework.Constants.SERVICE_PID);
		return pid == null ? null : pid.toString();
	}
}
