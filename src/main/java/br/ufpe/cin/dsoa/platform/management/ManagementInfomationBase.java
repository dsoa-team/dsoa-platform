package br.ufpe.cin.dsoa.platform.management;

import java.util.List;

import br.ufpe.cin.dsoa.platform.monitor.MonitoredService;

public interface ManagementInfomationBase {

	public MonitoredService getMonitoredService(String componentId, String specification);
	
	public List<MonitoredService> getMonitoredService(String componentId);
	
	public void addMonitoredService(MonitoredService monitoredService);
	
}
