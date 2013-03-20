package br.ufpe.cin.dsoa.management.service;

import javax.management.ObjectInstance;

import org.osgi.framework.ServiceRegistration;

public class ManagedService {


	private ServiceRegistration proxyReg;
	private ServiceRegistration monitorReg;
	private ObjectInstance mbeanReg;
	
	
	public ManagedService(ServiceRegistration proxyRegistration,
			ServiceRegistration monitorRegistration,
			ObjectInstance mbeanRegistration) {
		this.proxyReg = proxyRegistration;
		this.monitorReg = monitorRegistration;
		this.mbeanReg = mbeanRegistration;
	}
	
	public ServiceRegistration getProxyReg() {
		return proxyReg;
	}
	
	public ServiceRegistration getMonitorReg() {
		return monitorReg;
	}
	
	public ObjectInstance getMbeanReg() {
		return mbeanReg;
	}
	
	
}
