package br.ufpe.cin.dsoa.management;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.configurator.parser.metric.Metric;
import br.ufpe.cin.dsoa.metric.MetricCatalog;
import br.ufpe.cin.dsoa.metric.MetricId;
import br.ufpe.cin.dsoa.monitor.MonitoringService;

public class ManagementService {

	private ManagedServiceRegistry serviceRegistry;
	private MetricCatalog metricCatalog;
	private MonitoringService monitoringService;
	
	public synchronized void onArrival(ServiceReference reference) {
		ManagedService mgrService = new ManagedService(reference);
		monitoringService.startMonitoring(mgrService);
		serviceRegistry.addService(mgrService);
		mgrService.start();
	}

	public synchronized void onDeparture(ServiceReference ref) {
		String id = (String) ref.getProperty(Constants.SERVICE_ID);
		ManagedService mgrService = this.serviceRegistry.getService(id);
		monitoringService.stopMonitoring(mgrService);
		mgrService.stop();
	}

	public List<ManagedServiceMetadata> getManagedServicesMetadata() {
		List<ManagedServiceMetadata> metadata = new ArrayList<ManagedServiceMetadata>();
		for (ManagedService managedService : this.serviceRegistry.getServices()) {
			metadata.add(managedService.getMetadata());
		}
		return metadata;
	}
	
	public ManagedServiceMetadata getManagedServiceMetadata(String id){
		ManagedService service = this.serviceRegistry.getService(id);
		ManagedServiceMetadata metadata = null;
		if(null != service) {
			metadata = service.getMetadata();
		}
		
		return metadata;
	}

	public List<String> getMetricList() {
		List<String> metricList = new ArrayList<String>();
		for (Metric metric : this.metricCatalog.getMetrics()) {
			metricList.add(metric.toString());
		}
		return metricList;
	}
	
	public void addMetric(MetricId metricId, String id, String operationName) {
		ManagedService service = serviceRegistry.getService(id);
		monitoringService.setupMetricMonitor(metricId, service, operationName);
	}
}
