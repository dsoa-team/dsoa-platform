package br.ufpe.cin.dsoa.monitor;

import br.ufpe.cin.dsoa.contract.Sla;


public interface MonitoringService {
	public void startMonitoring(Sla sla);
}
