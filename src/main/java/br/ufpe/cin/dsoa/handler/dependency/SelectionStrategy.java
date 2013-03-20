package br.ufpe.cin.dsoa.handler.dependency;

public interface SelectionStrategy {

	ServiceModel select(SelectionListener listener, DependencyMetadata metadata);

}
