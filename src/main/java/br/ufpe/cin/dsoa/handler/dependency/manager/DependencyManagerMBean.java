package br.ufpe.cin.dsoa.handler.dependency.manager;

import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.handler.dependency.DependencyStatus;
import br.ufpe.cin.dsoa.handler.dependency.contract.Goal;
import br.ufpe.cin.dsoa.handler.dependency.contract.ServiceConsumer;
import br.ufpe.cin.dsoa.metric.MetricId;

public interface DependencyManagerMBean {
	/*public ServiceConsumer getConsumer() {
		return consumer;
	}

	public String getField() {
		return field;
	}

	public String getFilter() {
		return filter;
	}

	public List<Goal> getGoalList() {
		return goalList;
	}
	
	public boolean addGoal(Goal goal) {
		return this.goalList.add(goal);
	}
	
	public boolean removeGoal(Goal goal) {
		return this.goalList.remove(goal);
	}
	
	public List<MetricId> getMetricList() {
		return metricList;
	}
	
	public boolean addMetric(MetricId metricId) {
		return this.metricList.add(metricId);
	}
	
	public boolean removeMetric(MetricId metricId) {
		return this.metricList.remove(metricId);
	}

	public DependencyStatus getStatus() {
		return status;
	}

	public boolean isValid() {
		return this.status == DependencyStatus.RESOLVED;
	}

	public Class<?> getSpecification() {
		return this.specification;
	}
	
	public String getSpecificationName() {
		return this.specification.getName();
	}
	
	public List<ServiceReference> getBlackList() {
		return blackList;
	}*/
}
