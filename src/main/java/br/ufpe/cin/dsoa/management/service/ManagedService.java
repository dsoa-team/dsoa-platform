package br.ufpe.cin.dsoa.management.service;

import javax.management.ObjectInstance;

import org.osgi.framework.ServiceRegistration;

public class ManagedService {

	private String id;
	private ServiceRegistration proxyReg;
	private ServiceRegistration monitorReg;
	private ObjectInstance mbeanReg;
	
	public ManagedService(String id, ServiceRegistration proxyRegistration,
			ServiceRegistration monitorRegistration,
			ObjectInstance mbeanRegistration) {
		this.id = id;
		this.proxyReg = proxyRegistration;
		this.monitorReg = monitorRegistration;
		this.mbeanReg = mbeanRegistration;
	}
	
	public String getId() {
		return id;
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
