package br.ufpe.cin.dsoa.management;

import java.util.List;

import br.ufpe.cin.dsoa.monitor.MonitoredServiceMetadata;

/**
 * This intends to be the DSOA's management facade. 
 * 
 * @author dsoa-team
 *
 */
public interface ManagementService {

	List<String> getAttributeList();
	
	void addAttribute(String category, String metric, String servicePid, String operationName);
	
	void addAttributeMonitor(String servicePid, String attName, String attCategory, String operationName);
	
	public MonitoredServiceMetadata getManagedServiceMetadata(String id);
	
	public List<MonitoredServiceMetadata> getManagedServicesMetadata();
	
	public List<String> getAgentList();
}