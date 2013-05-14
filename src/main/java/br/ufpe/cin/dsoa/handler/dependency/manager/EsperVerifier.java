package br.ufpe.cin.dsoa.handler.dependency.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.Constraint;
import br.ufpe.cin.dsoa.metric.MetricComputingService;
import br.ufpe.cin.dsoa.metric.MetricId;
import br.ufpe.cin.dsoa.metric.MetricInstance;
import br.ufpe.cin.dsoa.metric.MetricInstanceImpl;

public class EsperVerifier implements Verifier {

	private MetricComputingService metricComputingService;
	private EventProcessingService epService;
	
	@Override
	public void configure(NotificationListener listener, String servicePid, List<Constraint> constraints) {
		for (Constraint constraint : constraints) {
			MetricInstance instance = this.getMetricInstance(servicePid, constraint);
			String statement = this.addFilters(instance.getMetric().getQuery(), servicePid, constraint);
			
			//this.epService.defineStatement(name, statement)
			//Statement stmt = buildStatement(consumerId, servicePid, constraint);
			//this.epService.defineStatement(name, statement)
			System.out.println(constraint);
		}
	}
	
	private String addFilters(String query, String servicePid, Constraint constraint) {
		StringBuffer statement = new StringBuffer(query);
		statement.append(" ");
		statement.append(this.getThreasholdClause(constraint));
		return null;
	}

	private String getThreasholdClause(Constraint constraint) {
		return " ";
	}
	
	private MetricInstance getMetricInstance(String servicePid, Constraint constraint) {
		// prefix.category.metric.target
		// metric.QoS.ResponseTime.priceAlert
		//[metric=qos.ResponseTime, operation=priceAlert, windowType=LENGTH, windowSize=20, expression=LT, threashold=800.0, weight=2]
		MetricInstance instance = null;
		List<String> nameParts = new ArrayList<String>();
		String metricName = constraint.getMetric();
		StringTokenizer st = new StringTokenizer(metricName, ".");
		while (st.hasMoreTokens()) {
			nameParts.add(st.nextToken());
		}
		MetricId metricId = new MetricId(nameParts.get(0), nameParts.get(1));
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
