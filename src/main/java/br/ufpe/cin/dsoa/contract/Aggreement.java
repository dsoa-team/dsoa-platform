package br.ufpe.cin.dsoa.contract;


public class Aggreement {
	private ServiceConsumer consumer;
	private ServiceProvider provider;
	private Service service;
	private AggreementOffer template;
	private AggreementMonitor monitor;
	
	
	public Aggreement(ServiceConsumer consumer, ServiceProvider provider,ServiceImpl service,
			AggreementOffer template, AggreementMonitor monitor) {
		super();
		this.consumer = consumer;
		this.provider = provider;
		this.service = service;
		this.template = template;
		this.monitor = monitor;
	}
}
