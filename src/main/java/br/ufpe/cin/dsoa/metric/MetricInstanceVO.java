package br.ufpe.cin.dsoa.metric;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;

public class MetricInstanceVO implements MetricInstance {

	private String category;
	private String name;
	private String serviceId;
	private String operationName;

	public MetricInstanceVO(String category, String name, String serviceId, String operationName) {
		super();
		this.category = category;
		this.name = name;
		this.serviceId = serviceId;
		this.operationName = operationName;
	}

	public String getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public String getServiceId() {
		return serviceId;
	}

	public String getOperationName() {
		return operationName;
	}

	@Override
	public Metric getMetric() {
		return null;
	}

	@Override
	public String getTarget() {
		return null;
	}

}
