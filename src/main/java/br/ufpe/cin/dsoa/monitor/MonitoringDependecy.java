package br.ufpe.cin.dsoa.monitor;

import java.util.List;

import javax.swing.SpringLayout.Constraints;

import br.ufpe.cin.dsoa.event.NotificationListener;

public interface MonitoringDependecy {

	void startMonitoring(List<Constraints> constraints, NotificationListener listener);
}
