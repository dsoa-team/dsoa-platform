package br.ufpe.cin.dsoa.agent.store;

import java.util.List;

import br.ufpe.cin.dsoa.agent.MetricAggregator;
import br.ufpe.cin.dsoa.agent.MetricComputer;

public interface StorageAgentService {

	public List<MetricComputer> getAvailableMetricAgents(String attribute);

	public List<MetricComputer> getAvailableMetricAgents();

	public MetricComputer getMetricAgent(String attribute);

	public List<MetricAggregator> getAvailableAggregatorAgents(String attribute);

	public List<MetricAggregator> getAvailableAggregatorAgents();

	public MetricAggregator getAggregatorAgent(String attribute);
}
