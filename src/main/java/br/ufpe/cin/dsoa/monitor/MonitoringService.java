package br.ufpe.cin.dsoa.monitor;

import br.ufpe.cin.dsoa.contract.DependencyMetadata;


public interface MonitoringService {
	public void startMonitoring(DependencyMetadata sla);
}
