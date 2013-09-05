package br.ufpe.cin.dsoa.util;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.api.attribute.AttributableId;

public class Util {
	
	public static boolean isRemote(ServiceReference reference) {
		return reference.getProperty(Constants.REMOTE_SERVICE) != null;
	}

	public static boolean isProxy(ServiceReference reference) {
		return reference.getProperty(Constants.SERVICE_PROXY) != null;
	}
	
	public static String getId(ServiceReference reference) {
		Object pid = reference.getProperty(org.osgi.framework.Constants.SERVICE_PID);
		if (pid == null) {
			pid =  reference.getProperty(org.osgi.framework.Constants.SERVICE_ID);
		}
		return pid.toString();
	}
	
	public static AttributableId getAttributableId(String serviceId, String operationName) {
		StringBuffer buf = new StringBuffer(serviceId);
		if (operationName != null) {
			buf.append(Constants.TOKEN).append(operationName);
		}
		return new AttributableId(buf.toString());
	}
	
}
