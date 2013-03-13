package br.ufpe.cin.dsoa.monitor;

import br.ufpe.cin.dsoa.DsoaConstants;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.Metric;

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
