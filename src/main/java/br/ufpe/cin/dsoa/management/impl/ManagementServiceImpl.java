package br.ufpe.cin.dsoa.management.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.management.ManagementService;
import br.ufpe.cin.dsoa.monitor.MonitoredService;
import br.ufpe.cin.dsoa.monitor.MonitoredServiceMetadata;
import br.ufpe.cin.dsoa.monitor.MonitoringService;

public class ManagementServiceImpl implements ManagementService {

	private MonitoringService monitoringService;
	
	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.ManagementService#onArrival(org.osgi.framework.ServiceReference)
	 */
	@Override
	public synchronized void onArrival(ServiceReference reference) {
		monitoringService.startMonitoring(reference);
	}

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.ManagementService#onDeparture(org.osgi.framework.ServiceReference)
	 */
	@Override
	public synchronized void onDeparture(ServiceReference reference) {
		monitoringService.stopMonitoring(reference);
	}

	public List<MonitoredServiceMetadata> getManagedServicesMetadata() {
		List<MonitoredServiceMetadata> metadata = new ArrayList<MonitoredServiceMetadata>();
		for (MonitoredService monitoredService : this.monitoringService.getMonitoredServices()) {
			metadata.add(monitoredService.getMetadata());
		}
		return metadata;
	}
	
	public MonitoredServiceMetadata getManagedServiceMetadata(String id){
		MonitoredService service = this.monitoringService.getMonitoredService(id);
		MonitoredServiceMetadata metadata = null;
		if(null != service) {
			metadata = service.getMetadata();
		}
		
		return metadata;
	}

	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.ManagementService#getMetricList()
	 */
	@Override
	public List<String> getMetricList() {
		/*List<String> metricList = new ArrayList<String>();
		for (Metric metric : this.metricCatalog.getMetrics()) {
			metricList.add(metric.toString());
		}
		return metricList;*/
		return null;
	}
	
	/* (non-Javadoc)
	 * @see br.ufpe.cin.dsoa.management.ManagementService#addMetric(br.ufpe.cin.dsoa.metric.MetricId, java.lang.String, java.lang.String)
	 */
	@Override
	public void addMetric(String category, String metric, String servicePid, String operationName) {
		//ManagedService mgdService = serviceRegistry.getService(servicePid);
	}
}
