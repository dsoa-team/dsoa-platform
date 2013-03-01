package br.ufpe.cin.dsoa.epcenter.configurator.parser.metric;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "metrics")
@XmlType(name = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetricList {

	public static final String CONFIG = "DSOA-INF/metric.xml";
	
	@XmlElement(name = "metric")
	private List<Metric> metrics;

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}
}
