package br.ufpe.cin.dsoa.management;

import br.ufpe.cin.dsoa.DsoaConstants;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.Metric;

public class StochasticVariable {

	private final Metric metric;
	private final String target;
	
	public StochasticVariable(Metric metric, String target) {
		this.metric = metric;
		this.target = target;
	}

	public Metric getMetric() {
		return metric;
	}

	public String getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return metric.getCategory() + DsoaConstants.TOKEN + metric.getName() + DsoaConstants.TOKEN + getTarget();
	}

}
