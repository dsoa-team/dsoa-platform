package br.ufpe.cin.dsoa.handler.dependency.manager;

import java.util.List;

import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.Goal;

public interface Verifier {
	void configure(NotificationListener listener, String servicePid, List<Goal> constraints);
}
