package br.ufpe.cin.dsoa.platform.service.selector;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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

	private volatile Map<String, Double> lastMetricValuePerServiceOperation = new HashMap<String, Double>();
	//private volatile Map<String, StatisticsComputingService> statistics = new HashMap<String, StatisticsComputingService>();
	private EventProcessingService epService;
	private AttributeEventMapperCatalog attributeMapperCatalog;
	private EventTypeCatalog eventTypeCatalog;
	private static Logger avaQosLogger;
	private static Logger rtQosLogger;
	private static Logger matrixLogger;
	private static Map<String,Logger> logMap = new HashMap<String,Logger>();
	
	
	{
		java.util.logging.Formatter f = new java.util.logging.Formatter() {
			
			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}
		};
		
		matrixLogger = Logger.getLogger("MatrixLogger");
		try {
			FileHandler logHandler = new FileHandler("logs/app/MatrixLogger.log");
			logHandler.setFormatter(f);
			matrixLogger.setUseParentHandlers(false);
			matrixLogger.addHandler(logHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	{
		java.util.logging.Formatter f1 = new java.util.logging.Formatter() {
			
			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}
		};
		avaQosLogger = Logger.getLogger("AvaQoSLogger");
		try {
			FileHandler logHandler = new FileHandler("logs/app/AvaQoSLogger.log");
			logHandler.setFormatter(f1);
			avaQosLogger.setUseParentHandlers(false);
			avaQosLogger.addHandler(logHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	{
		java.util.logging.Formatter f = new java.util.logging.Formatter() {
			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder(1000);
				builder.append(formatMessage(record));
				builder.append("\n");
				return builder.toString();
			}
		};
		rtQosLogger = Logger.getLogger("RtQoSLogger");
		try {
			FileHandler logHandler = new FileHandler("logs/app/RtQoSLogger.log");
			logHandler.setFormatter(f);
			rtQosLogger.setUseParentHandlers(false);
			rtQosLogger.addHandler(logHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logMap.put("qos.AvgAvailability", avaQosLogger);
		logMap.put("qos.AvgResponseTime", rtQosLogger);
	}
	
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
			
			Double val = Double.valueOf(attValue.getValue().toString());
			lastMetricValuePerServiceOperation.put(key, Double.valueOf(val));
			DecimalFormat df = new DecimalFormat("#.###");
			Logger log = logMap.get(attValue.getAttribute().getId());
			log.info(System.currentTimeMillis() + ":" + source + ":" + attValue.getAttribute().getId() + ":" + df.format(val)+"\n");			
			/*
			 * PREVIOUS VERSION STORED THE AVG OF THE ENTIRE HISTORY
			 * StatisticsComputingService stats = statistics.get(key);
			if (stats == null) {
				stats = new StatisticsComputingService();
				statistics.put(key, stats);
			}
			Double val = Double.valueOf(attValue.getValue().toString());
			stats.addValue(val);
			DecimalFormat df = new DecimalFormat("#.###");
			// service.operation:attribute:curr_val:hist_mean
			avaQosLogger.info(System.currentTimeMillis() + ":" + source + ":" + attValue.getAttribute().getId() + ":" + df.format(val) + ": " + df.format(stats.getMean()));*/
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
				filteredCandidates, lastMetricValuePerServiceOperation);

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
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuffer buff = new StringBuffer("[");
		for(int i = 0; i < normalized.length; i++) {
			buff.append("[");
			for (int j = 0; j < normalized[0].length; j++) {
				buff.append(df.format(normalized[i][j])).append(",");
			}
			buff.replace(buff.lastIndexOf(","), buff.lastIndexOf(",")+1, "]");
		}
		buff.append("]");
		
		matrixLogger.info(System.currentTimeMillis() + " : " + buff.toString() + " : " + weighted.keySet() +"\n");
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