package br.ufpe.cin.dsoa.contract;

import br.ufpe.cin.dsoa.handlers.dependency.DependencyMetadata;


public class Aggreement {
	private ServiceConsumer consumer;
	private ServiceProvider provider;
	private Service service;
	private DependencyMetadata template;
	private AggreementMonitor monitor;
	
	
	public Aggreement(ServiceConsumer consumer, ServiceProvider provider,ServiceImpl service,
			DependencyMetadata template, AggreementMonitor monitor) {
		super();
		this.consumer = consumer;
		this.provider = provider;
		this.service = service;
		this.template = template;
		this.monitor = monitor;
	}
}
