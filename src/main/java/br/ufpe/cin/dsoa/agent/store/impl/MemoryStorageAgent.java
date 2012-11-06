package br.ufpe.cin.dsoa.agent.store.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import br.ufpe.cin.dsoa.agent.MetricAggregator;
import br.ufpe.cin.dsoa.agent.MetricComputer;
import br.ufpe.cin.dsoa.agent.store.StorageAgentService;

public class MemoryStorageAgent implements StorageAgentService {

	private BundleContext ctx;

	private Map<String, List<ServiceReference>> metricComputerAgents;
	private Map<String, List<ServiceReference>> metricAggregatorAgents;

	private final String AGENT_ID = "qos.name";

	/**
	 * iPOJO callback post-registration used to open agent service traker
	 * 
	 * @param reference
	 */
	public void listen(ServiceReference reference) {

		this.ctx = reference.getBundle().getBundleContext();

		metricComputerAgents = new HashMap<String, List<ServiceReference>>();
		metricAggregatorAgents = new HashMap<String, List<ServiceReference>>();

		new ServiceTracker(ctx, MetricComputer.class.getName(),
				new MetricComputerTracker()).open();

		new ServiceTracker(ctx, MetricAggregator.class.getName(),
				new MetricAggregatorTracker()).open();
	}

	public List<MetricComputer> getAvailableMetricAgents(String attribute) {

		List<MetricComputer> agents = new ArrayList<MetricComputer>();

		for (ServiceReference reference : metricComputerAgents.get(attribute)) {
			agents.add((MetricComputer) ctx.getService(reference));
		}

		return agents;
	}

	public List<MetricComputer> getAvailableMetricAgents() {

		List<MetricComputer> agents = new ArrayList<MetricComputer>();

		for (List<ServiceReference> references : metricComputerAgents
				.values()) {
			for (ServiceReference reference : references) {
				agents.add((MetricComputer) ctx.getService(reference));
			}
		}
		return agents;
	}

	public MetricComputer getMetricAgent(String attribute) {

		ServiceReference reference = null;
		MetricComputer agent = null;

		if (metricComputerAgents.containsKey(attribute)) {
			reference = metricComputerAgents.get(attribute).get(0);
			agent = (MetricComputer) ctx.getService(reference);
		}

		return agent;
	}

	public List<MetricAggregator> getAvailableAggregatorAgents(String attribute) {

		List<MetricAggregator> agents = new ArrayList<MetricAggregator>();

		for (ServiceReference reference : metricAggregatorAgents
				.get(attribute)) {
			agents.add((MetricAggregator) ctx.getService(reference));
		}

		return agents;
	}

	public List<MetricAggregator> getAvailableAggregatorAgents() {

		List<MetricAggregator> agents = new ArrayList<MetricAggregator>();

		for (List<ServiceReference> references : metricAggregatorAgents
				.values()) {
			for (ServiceReference reference : references) {
				agents.add((MetricAggregator) ctx.getService(reference));
			}
		}
		return agents;
	}

	public MetricAggregator getAggregatorAgent(String attribute) {

		ServiceReference reference = null;
		MetricAggregator agent = null;

		if (metricAggregatorAgents.containsKey(attribute)) {
			reference = metricAggregatorAgents.get(attribute).get(0);
			agent = (MetricAggregator) ctx.getService(reference);
		}
		return agent;
	}

	class MetricComputerTracker implements ServiceTrackerCustomizer {

		public Object addingService(ServiceReference reference) {

			String key = (String) reference.getProperty(AGENT_ID);

			if (metricComputerAgents.containsKey(key)) {
				metricComputerAgents.get(key).add(reference);
			} else {
				List<ServiceReference> references = new ArrayList<ServiceReference>();
				references.add(reference);
				metricComputerAgents.put(key, references);
			}

			return reference;
		}

		public void modifiedService(ServiceReference reference, Object service) {
			System.out.println("modifield");
		}

		public void removedService(ServiceReference reference, Object service) {
			String key = (String) reference.getProperty(AGENT_ID);
			metricComputerAgents.get(key).remove(reference);
		}

	}

	class MetricAggregatorTracker implements ServiceTrackerCustomizer {

		public Object addingService(ServiceReference reference) {
			String key = (String) reference.getProperty(AGENT_ID);

			if (metricAggregatorAgents.containsKey(key)) {
				metricAggregatorAgents.get(key).add(reference);
			} else {
				List<ServiceReference> references = new ArrayList<ServiceReference>();
				references.add(reference);
				metricAggregatorAgents.put(key, references);
			}

			return reference;
		}

		public void modifiedService(ServiceReference reference, Object service) {
			System.out.println("modifield");
		}

		public void removedService(ServiceReference reference, Object service) {
			String key = (String) reference.getProperty(AGENT_ID);
			metricAggregatorAgents.get(key).remove(reference);
		}
	}
}