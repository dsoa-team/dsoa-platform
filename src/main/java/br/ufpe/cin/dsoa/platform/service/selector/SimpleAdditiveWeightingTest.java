package br.ufpe.cin.dsoa.platform.service.selector;


public class SimpleAdditiveWeightingTest { //implements RankStrategy {

	//<serviceId><opId><metric>
/*	private Map<String,StatisticsComputingService> statistics = new HashMap<String, StatisticsComputingService>();
	private EventProcessingService epService;
	private AttributeEventMapperCatalog attributeMapperCatalog;
	
	public void start() {
		for(final AttributeEventMapper attMapper : attributeMapperCatalog.getAttributeEventMapperList()) {
			EventType eventType = attMapper.getEventType();
			Subscription subscription = new Subscription(eventType, null);

			EventConsumer consumer = new EventConsumer() {

				@Override
				public void handleEvent(Event event) {
					String source = (String)event.getProperty(Constants.EVENT_SOURCE).getValue();
					StatisticsComputingService stats = statistics.get(source);
					if (stats == null) {
						stats = new StatisticsComputingService();
						statistics.put(source, stats);
					}
					AttributeValue attValue = attMapper.convertToAttribute(event);
					stats.addValue(Long.valueOf(attValue.getValue().toString()));
				}
				
				@Override
				public String getComponentInstanceName() {
					return "qosLogger";
				}
			};

			this.epService.subscribe(consumer, subscription, true); 
		}
	}

	public void stop() {
		
	}
	
	public ServiceInstance ranking(List<Constraint> constraints,
			List<ServiceInstance> candidates) {
		List<ServiceInstance> realCandidates = filterCandidates (constraints, candidates);
		
		//Map<Constraint, Map<ServiceInstance, Double>> constMap = new HashMap<Constraint, Map<ServiceInstance, Double>>();

		double [][] mapVal = new double[constraints.size()][];
		for(int j =0; j < constraints.size(); j++) {
			//HashMap<ServiceInstance, Double> perCandMap = new HashMap<ServiceInstance, Double>();
			//constMap.put(req, perCandMap);
			Constraint req = constraints.get(j);
			double candCon[] = new double[realCandidates.size()];
			for (int i = 0; i < realCandidates.size(); i++) {
				ServiceInstance instance = realCandidates.get(i);
				
				for (Constraint cons : instance.getPort().getServiceSpecification().getNonFunctionalSpecification().getConstraints()) {
					if(areEquivalent(req,cons)) {
						AttributableId attId = new AttributableId(instance.getName(), cons.getOperation());
						StatisticsComputingService stats = statistics.get(attId.getId());
						if (stats == null) {
							stats = new StatisticsComputingService();
							statistics.put(attId.getId(), stats);
							stats.addValue(cons.getThreashold());
						}
						candCon[i] = stats.getMean();
						break;
					}
				}
			}
			mapVal[j] = candCon;
		}
		return this.rank(mapVal);
	}
	
	private ServiceInstance rank(double[][] map) {
		double [] sum = new double[map.length];
		
		for (int i = 0; i < sum.length; i++) {
			sum[i] = 0;
			for (int j = 0; j < map[i][j]; j++) {
				sum[i] += map[j][i];
			}
		}
		return null;
	}

	public boolean areEquivalent(Constraint req, Constraint off) {
		if (req.getAttributeId().equals(off.getAttributeId())) {
			if (
				(req.getOperation() != null && off.getOperation()!= null && req.getOperation().equals(off.getOperation())) ||
				(req.getOperation() == null && off.getOperation() == null)){
				return true;
			}
			return false;
		}
		return false;
	}
	
	
	public ServiceInstance rankingFns(List<Constraint> constraints,
			List<ServiceInstance> candidates) {

		List<ServiceInstance> filteredCandidates =  filterCandidates(constraints, candidates);
		double[][] normalized = Normalizer.normalize(constraints, filteredCandidates, this.statistics);
		
		Map<Double, ServiceInstance> weighted = new HashMap<Double, ServiceInstance>();
		
		//x indice dos candidates
		for(int x = 0; x < normalized.length; x++) {
			List<Constraint> candidateConstraints = filteredCandidates.get(x).getPort().getServiceSpecification().getNonFunctionalSpecification().getConstraints();
			Map<String, Constraint> mapConstraint = Normalizer.toMap(candidateConstraints);
			
			double sum = 0;
			for(int y = 0; y < normalized[0].length; y++) {
				Constraint requiredConstraint = constraints.get(y);
				double weight = mapConstraint.get(Normalizer.constraintKey(requiredConstraint)).getWeight();
				sum += weight * normalized[x][y];
			}
			
			weighted.put(sum, filteredCandidates.get(x));
		}
		
		double max = Collections.max(weighted.keySet());
		
		return weighted.get(max);
	}

	*//**
	 * Filtra candidatos que não dispoem de informacoes a respeito do atritudo
	 * requerido
	 * 
	 * @param constraints
	 * @param candidates
	 * @return
	 *//*
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
	}*/
	
/*	public ServiceInstance ranking(List<Constraint> constraints,
			List<ServiceInstance> candidates) {
		
		for(ServiceInstance candidate : candidates) {
			String id = candidate.getName();
			List<Constraint> cons = candidate.getPort().getServiceSpecification().getNonFunctionalSpecification().getConstraints();
			for(Constraint c : cons) {
				String op = c.getOperation();
				if (op != null) {
					id += op;
					if (this.)
				}
			}
		}
		
		return null;
	}*/

}
