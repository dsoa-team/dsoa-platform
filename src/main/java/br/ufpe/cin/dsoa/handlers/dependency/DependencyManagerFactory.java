package br.ufpe.cin.dsoa.handlers.dependency;

import org.apache.felix.ipojo.metadata.Element;

import br.ufpe.cin.dsoa.contract.ServiceConsumer;

public interface DependencyManagerFactory {
	public DependencyManager create(ServiceConsumer consumer, Element configuration);
}
