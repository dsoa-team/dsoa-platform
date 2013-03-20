package br.ufpe.cin.dsoa.management.metric;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.util.DsoaConstants;

public class MetricInstance {

	private final Metric metric;
	private final String serviceId;
	private final String operationName;
	
	public MetricInstance(Metric metric, String serviceId, String operationName) {
		this.metric = metric;
		this.serviceId = serviceId;
		this.operationName = operationName;
	}

	public Metric getMetric() {
		return metric;
	}

	public String getServiceId() {
		return serviceId;
	}
	
	public String getOperationName() {
		return operationName;
	}

	public String getTarget() {
		return toString();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(metric.toString());
		sb.append(DsoaConstants.TOKEN).append(serviceId);
		if (null != this.operationName) {
			sb.append(DsoaConstants.TOKEN).append(operationName);
		}
		return  sb.toString();
	}

}
