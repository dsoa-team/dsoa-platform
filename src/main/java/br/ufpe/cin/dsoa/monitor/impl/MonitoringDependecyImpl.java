package br.ufpe.cin.dsoa.monitor.impl;

import java.util.List;

import javax.swing.SpringLayout.Constraints;

import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.monitor.MonitoringDependecy;

public class MonitoringDependecyImpl implements MonitoringDependecy {

	private EventProcessingService eventProcessingService;
	
	@Override
	public void startMonitoring(List<Constraints> constraints,
			NotificationListener listener) {

		//TODO
	}

}
