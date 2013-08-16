package br.ufpe.cin.dsoa.metric;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.util.Constants;

public class MetricInstanceImpl implements MetricInstance {

	private final Metric metric;
	private final String serviceId;
	private final String operationName;
	
	public MetricInstanceImpl(Metric metric, String serviceId, String operationName) {
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
		sb.append(Constants.TOKEN).append(serviceId);
		if (null != this.operationName) {
			sb.append(Constants.TOKEN).append(operationName);
		}
		return  sb.toString();
	}

	public String getCategory() {
		// TODO Auto-generated method stub
		return metric.getCategory();
	}

	public String getName() {
		// TODO Auto-generated method stub
		return metric.getName();
	}

}
