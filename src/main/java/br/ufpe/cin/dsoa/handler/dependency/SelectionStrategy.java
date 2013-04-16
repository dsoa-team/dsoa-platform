package br.ufpe.cin.dsoa.handler.dependency;

import br.ufpe.cin.dsoa.contract.Service;

public interface SelectionStrategy {

	Service select(SelectionListener listener, DependencyMetadata metadata);

}
