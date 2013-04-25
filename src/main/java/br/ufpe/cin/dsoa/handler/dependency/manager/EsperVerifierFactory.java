package br.ufpe.cin.dsoa.handler.dependency.manager;

import java.util.List;

import br.ufpe.cin.dsoa.event.EventProcessingService;
import br.ufpe.cin.dsoa.event.NotificationListener;
import br.ufpe.cin.dsoa.handler.dependency.contract.Constraint;

public class EsperVerifierFactory implements VerifierFactory {

	private EventProcessingService epService;
	
	@Override
	public void configure(NotificationListener listener, String consumerId, String servicePid, List<Constraint> constraints) {
		for (Constraint constraint : constraints) {
			//Statement stmt = buildStatement(consumerId, servicePid, constraint);
			//this.epService.defineStatement(name, statement)
		}
	}

	private String buildStatement(String consumerId, String servicePid, Constraint constraint) {
		// TODO Auto-generated method stub
		return null;
	}

}
