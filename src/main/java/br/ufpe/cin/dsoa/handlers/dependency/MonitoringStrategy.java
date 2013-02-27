package br.ufpe.cin.dsoa.handlers.dependency;

public interface MonitoringStrategy {

	Object monitor(ViolationListener listener, ServiceModel serviceModel, DependencyMetadata metadata);

}
