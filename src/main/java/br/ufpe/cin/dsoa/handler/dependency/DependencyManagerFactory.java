package br.ufpe.cin.dsoa.handler.dependency;

import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.contract.ServiceConsumer;

public interface DependencyManagerFactory {
	public Dependency create(ServiceConsumer consumer, Element configuration);
}
