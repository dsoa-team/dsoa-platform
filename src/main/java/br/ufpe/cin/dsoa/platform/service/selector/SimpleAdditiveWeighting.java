package br.ufpe.cin.dsoa.platform.service.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.dsoa.api.attribute.AttributeValue;
import br.ufpe.cin.dsoa.api.attribute.mapper.AttributeEventMapper;
import br.ufpe.cin.dsoa.api.event.Event;
import br.ufpe.cin.dsoa.api.event.EventConsumer;
import br.ufpe.cin.dsoa.api.event.EventProcessingService;
import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.api.event.EventTypeCatalog;
import br.ufpe.cin.dsoa.api.event.Subscription;
import br.ufpe.cin.dsoa.api.selector.RankStrategy;
import br.ufpe.cin.dsoa.api.service.Constraint;
import br.ufpe.cin.dsoa.api.service.ServiceInstance;
import br.ufpe.cin.dsoa.platform.attribute.AttributeEventMapperCatalog;
import br.ufpe.cin.dsoa.platform.attribute.impl.AttributeManager;
import br.ufpe.cin.dsoa.util.Constants;

public class SimpleAdditiveWeighting implements RankStrategy {

	private Map<String, StatisticsComputingService> statistics = new HashMap<String, StatisticsComputingService>();
	private EventProcessingService epService;
	private AttributeEventMapperCatalog attributeMapperCatalog;
	private EventTypeCatalog eventTypeCatalog;

	private class QoSLogger implements EventConsumer {

		private AttributeEventMapper attMapper;

		public QoSLogger(AttributeEventMapper attMapper) {
			this.attMapper = attMapper;
		}

		@Override
		public void handleEvent(Event event) {
			AttributeValue attValue = attMapper.convertToAttribute(event);
			String source = (String) event.getProperty(Constants.EVENT_SOURCE)
					.getValue();
			String key = String.format("%s.%s",source,attValue.getAttribute().getId());
			StatisticsComputingService stats = statistics.get(key);
			if (stats == null) {
				stats = new StatisticsComputingService();
				statistics.put(key, stats);
			}
			stats.addValue(Double.valueOf(attValue.getValue().toString()));
		}

		@Override
		public String getComponentInstanceName() {
			// TODO Auto-generated method stub
			return "DsoaQoSRegistryLoger";
		}

	};

	public void start() {
		EventType eventType = this.eventTypeCatalog.get(Constants.NEW_MONITORING_DIRECTIVE_EVENT);
		Subscription mapperSubscription = new Subscription(eventType, null);
		EventConsumer mapperConsumer = new EventConsumer() {

			@Override
			public void handleEvent(Event event) {
				String eventName = event.getProperty("event").getValue().toString();
				String cat = event.getProperty("category").getValue().toString();
				String att = event.getProperty("attribute").getValue().toString();
				EventType eventTypeMet = eventTypeCatalog.get(eventName);
				Subscription subscription = new Subscription(eventTypeMet, null);
				AttributeEventMapper attMapper = attributeMapperCatalog.getAttributeEventMapper(AttributeManager.format(cat, att));
				EventConsumer qosLogger = new QoSLogger(attMapper);
				epService.subscribe(qosLogger, subscription);
			}

			@Override
			public String getComponentInstanceName() {
				// TODO Auto-generated method stub
				return null;
			};
		};
		epService.subscribe(mapperConsumer, mapperSubscription, true);
	}

	@Override
	public ServiceInstance ranking(List<Constraint> constraints,
			List<ServiceInstance> candidates) {
		// Filter undesired services
		List<ServiceInstance> filteredCandidates = filterCandidates(
				constraints, candidates);

		// Normalize attribute values
		double[][] normalized = Normalizer.normalize(constraints,
				filteredCandidates, statistics);

		// Totalize the weight
		double sumWeight = 0;
		for (Constraint c : constraints) {
			sumWeight += c.getWeight();
		}

		Map<Double, ServiceInstance> weighted = new HashMap<Double, ServiceInstance>();

		// x indice dos candidates
		for (int x = 0; x < normalized.length; x++) {
			double sum = 0;
			for (int y = 0; y < normalized[0].length; y++) {
				Constraint requiredConstraint = constraints.get(y);
				double normWeight = requiredConstraint.getWeight() / sumWeight;
				sum += normWeight * normalized[x][y];
			}

			weighted.put(sum, filteredCandidates.get(x));
		}

		double max = Collections.max(weighted.keySet());

		return weighted.get(max);
	}

	/**
	 * Filtra candidatos que não dispoem de informacoes a respeito do atritudo
	 * requerido
	 * 
	 * @param constraints
	 * @param candidates
	 * @return
	 */
	private List<ServiceInstance> filterCandidates(
			List<Constraint> constraints, List<ServiceInstance> candidates) {

		List<ServiceInstance> filtered = new ArrayList<ServiceInstance>();

		for (ServiceInstance candidate : candidates) {
			List<Constraint> candidateConstraints = candidate.getPort()
					.getServiceSpecification().getNonFunctionalSpecification()
					.getConstraints();
			Map<String, Constraint> mapConstraint = Normalizer
					.toMap(candidateConstraints);

			boolean valid = true;
			for (Constraint required : constraints) {
				if (!mapConstraint.containsKey(Normalizer
						.constraintKey(required))) {
					valid = false;
				}
			}

			if (valid) {
				filtered.add(candidate);
			} else {
				valid = true;
			}

		}

		return filtered;
	}

}