package br.ufpe.cin.dsoa.management;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.osgi.framework.ServiceReference;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;

import br.ufpe.cin.dsoa.epcenter.EventProcessingCenter;
import br.ufpe.cin.dsoa.epcenter.configurator.parser.metric.Metric;

public class ServiceMonitor implements Monitorable {

	private MetricCatalog metricCatalog;
	private EventProcessingCenter epCenter;

	private ServiceReference reference;
	private Map<String, StochasticVariableMonitor> stochasticVariableMap;

	public ServiceMonitor(EventProcessingCenter epCenter,
			MetricCatalog catalog, ServiceReference reference) {
		this.epCenter = epCenter;
		this.metricCatalog = catalog;
		this.reference = reference;
		this.stochasticVariableMap = new HashMap<String, StochasticVariableMonitor>();
		this.startMonitoring(reference);
	}

	// QoS.ResponseTime.priceAlert
	private void startMonitoring(ServiceReference reference) {
		String keys[] = reference.getPropertyKeys();

		for (String key : keys) {
			if (key.toLowerCase().startsWith(Metric.METRIC_PREFIX)) {
				key = key.substring(Metric.METRIC_PREFIX.length());
				StochasticVariableTemplate template = this
						.defineStochasticVariable(key);
				Metric metric = metricCatalog.getMetric(template.getMetricId());
				StochasticVariable variable = new StochasticVariable(metric,
						template.getTarget());
				if (null != metric) {
					StochasticVariableMonitor variableMonitor = new StochasticVariableMonitor(
							variable);
					this.stochasticVariableMap.put(variableMonitor.getPath(),
							variableMonitor);
					this.epCenter.subscribe(metric.getQuery(), variableMonitor);
				}
			}
		}
	}

	private StochasticVariableTemplate defineStochasticVariable(String key) {
		StringTokenizer tokenizer = new StringTokenizer(key, ".");
		int ntokens = tokenizer.countTokens();
		String category = null;
		String name = null;
		String target = null;
		if (ntokens < 2 || ntokens > 3) {
			throw new IllegalArgumentException(key
					+ " is not a valid metric name!");
		} else {
			category = tokenizer.nextToken();
			name = tokenizer.nextToken();
			if (ntokens == 3) {
				target = tokenizer.nextToken();
			}
		}
		return new StochasticVariableTemplate(new MetricId(category, name),
				target);
	}

	public String[] getStatusVariableNames() {
		String[] variableNames = new String[stochasticVariableMap.size()];
		int i = 0;
		for (String key : stochasticVariableMap.keySet()) {
			variableNames[i++] = key;
		}
		return variableNames;
	}

	public StatusVariable getStatusVariable(String id)
			throws IllegalArgumentException {
		if (stochasticVariableMap.containsKey(id)) {
			return stochasticVariableMap.get(id).getStatusVariable();
		}
		throw new IllegalArgumentException("Variable " + id + " does not exist");
	}

	public boolean notifiesOnChange(String id) throws IllegalArgumentException {
		return false;
	}

	public boolean resetStatusVariable(String id)
			throws IllegalArgumentException {
		return false;
	}

	public String getDescription(String id) throws IllegalArgumentException {
		if (stochasticVariableMap.containsKey(id)) {
			return stochasticVariableMap.get(id).getDescription();
		}
		throw new IllegalArgumentException("Variable " + id + " does not exist");
	}

}
