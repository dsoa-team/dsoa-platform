package br.ufpe.cin.dsoa.handler.dependency.manager;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.Constraint;
import br.ufpe.cin.dsoa.metric.MetricComputingService;
import br.ufpe.cin.dsoa.metric.MetricId;
import br.ufpe.cin.dsoa.metric.MetricInstance;
import br.ufpe.cin.dsoa.metric.MetricInstanceImpl;
import br.ufpe.cin.dsoa.metric.MetricParser;
import br.ufpe.cin.dsoa.util.Util;

public class EsperVerifier implements Verifier {

	private MetricComputingService metricComputingService;
	private EventProcessingService epService;
	
	@Override
	public void configure(NotificationListener listener, String servicePid, List<Constraint> constraints) {
		for (Constraint constraint : constraints) {
			MetricInstance instance = this.getMetricInstance(servicePid, constraint);
			String statement = this.addFilters(instance.getMetric().getQuery(), servicePid, constraints);
			
			//this.epService.defineStatement(name, statement)
			//Statement stmt = buildStatement(consumerId, servicePid, constraint);
			//this.epService.defineStatement(name, statement)
			System.out.println(constraint);
		}
	}
	
	private String addFilters(String query, String servicePid, List<Constraint> constraints) {
		StringBuffer statement = new StringBuffer(query);
		return null;
	}

	private MetricInstance getMetricInstance(String servicePid, Constraint constraint) {
		// prefix.category.metric.target
		// metric.QoS.ResponseTime.priceAlert
		//[metric=qos.ResponseTime, operation=priceAlert, windowType=LENGTH, windowSize=20, expression=LT, threashold=800.0, weight=2]
		MetricInstance instance = null;
		String metricName = constraint.getMetric();
		String[] nameParts = metricName.split(".");
		MetricId metricId = new MetricId(nameParts[0],nameParts[1]);
		Metric metric = metricComputingService.getMetric(metricId);
		if (null != metric) {
			instance = new MetricInstanceImpl(metric, servicePid, constraint.getOperation());
		}
		return instance;
	}

	private String buildStatement(String consumerId, String servicePid, Constraint constraint) {
		// TODO Auto-generated method stub
		return null;
	}

}
