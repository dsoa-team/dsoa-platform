package br.ufpe.cin.dsoa;

import org.apache.felix.ipojo.InstanceManager;

import br.ufpe.cin.dsoa.handlers.dependency.DependencyManager;
import br.ufpe.cin.dsoa.handlers.dependency.ServiceDependency;
import br.ufpe.cin.dsoa.handlers.provider.ProviderManager;
import br.ufpe.cin.dsoa.handlers.provider.ProviderMetadata;

public class AdaptationManager {

	public static void manage(ServiceDependency serviceDependency) {
		DependencyManager manager = new DependencyManager(serviceDependency);
		serviceDependency.setDependencyManager(manager);
	}

	public static Object createProxy(InstanceManager manager,
			ProviderMetadata metadata) {
		return ProviderManager.createProxy(manager, metadata);
	}
}
