package br.ufpe.cin.dsoa.monitor;

import br.ufpe.cin.dsoa.handlers.dependency.DependencyMetadata;

public interface MonitoringService {
	public void startMonitoring(DependencyMetadata sla);
}
