package br.ufpe.cin.dsoa.monitor;

public class MonitoringContext {

	private String clientId;
	private String serviceId;
	
	public MonitoringContext(String serviceId, String clientId) {
		super();
		this.serviceId = serviceId;
		this.clientId = clientId;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getId() {
		return this.getClientId() + "." + this.getServiceId();
	}
}
