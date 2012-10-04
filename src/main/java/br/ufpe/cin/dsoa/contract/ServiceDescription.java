package br.ufpe.cin.dsoa.contract;

public class ServiceDescription {

	private Object service;
	private Provider provider;

	public Object getService() {
		return this.service;
	}

	public Provider getProvider() {
		return this.provider;
	}

	public void setService(Object service) {
		this.service = service;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

}
