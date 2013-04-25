package br.ufpe.cin.dsoa.handler.dependency.manager;

import java.util.List;

import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.Constraint;

public interface VerifierFactory {
	void configure(NotificationListener listener, String consumerId, String servicePid, List<Constraint> constraints);
}
