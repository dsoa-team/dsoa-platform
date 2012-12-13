package br.ufpe.cin.dsoa.monitor.impl;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.AggreementOffer;
import br.ufpe.cin.dsoa.epcenter.EventConsumer;
import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.monitor.MonitoringConfiguration;
import br.ufpe.cin.dsoa.monitor.MonitoringContext;
import br.ufpe.cin.dsoa.monitor.MonitoringService;

public class MonitoringServiceImpl implements MonitoringService {

	private final BundleContext bundleContext;
	private EventProcessingCenter eventProcessingCenter;
	private MonitoringConfiguration configuration;

	public MonitoringServiceImpl(BundleContext bundleContext) {
		this.bundleContext = bundleContext;

		ServiceReference serviceReference = this.bundleContext
		.getServiceReference(EventProcessingCenter.class.getName());
		if (serviceReference != null) {
			this.eventProcessingCenter = (EventProcessingCenter) this.bundleContext
			.getService(serviceReference);
		}
	}

	public void startMonitoring(AggreementOffer sla) {
		/*this.configuration = new MonitoringConfiguration(new MonitoringContext(sla.getConsumerPid(),
				sla.getServiceReference().getProperty("provider.pid").toString()), this);

		for (Slo slo : this.sla.getSlos()) {
			MonitoringConfigurationItem item = new MonitoringConfigurationItem(
					slo.getOperation(), slo.getAttribute(), slo.getExpression()
							.getOperator(), slo.getValue(), slo.getStatistic(),
					slo.getWindowUnit(), slo.getWindowValue(), configuration);
			this.configuration.addConfigurationItem(item);
		}
		monitor.startMonitoring(configuration);*/
		
	}
	
	class Listener implements EventConsumer {
		// Configurações para otimizar a escrita na base escrevendo em uma thread separada
		private int poolSize = 30;
		private int maxPoolSize = 50;
		private long keepAliveTime = 10;
		final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(
	            100);
		private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize,
                keepAliveTime, TimeUnit.SECONDS, queue);
//
		double countRequest = 0;
		double countErrorIoException = 0;

		double countRequestPerOperation = 0;
		double countErrorBusinessException = 0;

		String consumerId = null;
		String serviceId = null;
		String operationName = null;

		@SuppressWarnings("rawtypes")
		public void receive(Map result, Object userObject, String statementName) {

		}

	}


	/*
	@Override
	public void startMonitoring(MonitoringConfiguration configuration) {
		for (MonitoringConfigurationItem item : configuration.getItens().values()) {

			this.eventProcessingCenter.defineStatement(item.getStatementName(),item.getStatement());
			this.eventProcessingCenter.subscribe(item.getStatementName(),configuration);

			if (item.getSecondaryStatements().size() != 0) {
				Listener l = new Listener();
				//System.out.println("Itens Secundarios");
				for (SecondaryStatement s : item.getSecondaryStatements()) {
					this.eventProcessingCenter.defineStatement(s.getName(),s.getSecondaryStatement());
					this.eventProcessingCenter.subscribe(s.getName(), l);
				}
			}

		}

	} */

}
