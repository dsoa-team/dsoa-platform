package br.ufpe.cin.dsoa.metric;

import java.util.StringTokenizer;

public class MetricParser {

	private String metricCategory;
	private String metricName;
	private String operationName;
	private MetricId metricId;
	
	public MetricParser(String key) {
		this.parse(key);
	}

	private void parse(String key) {
		StringTokenizer tokenizer = new StringTokenizer(key, ".");
		int ntokens = tokenizer.countTokens();
		if (ntokens < 2 || ntokens > 3) {
			throw new IllegalArgumentException(key
					+ " is not a valid metric name!");
		} else {
			metricCategory = tokenizer.nextToken();
			metricName = tokenizer.nextToken();
			if (ntokens == 3) {
				operationName = tokenizer.nextToken();
			}
			metricId = new MetricId(metricCategory, metricName);
		}
	}
	
	
	public String getOperationName() {
		return operationName;
	}

	public String getMetricCategory() {
		return metricCategory;
	}

	public String getMetricName() {
		return metricName;
	}

	public MetricId getMetricId() {
		return metricId;
	}

}
