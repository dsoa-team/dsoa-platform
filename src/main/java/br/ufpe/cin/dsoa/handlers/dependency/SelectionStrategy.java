package br.ufpe.cin.dsoa.handlers.dependency;

public interface SelectionStrategy {

	ServiceModel select(SelectionListener listener, DependencyMetadata metadata);

}
