package br.ufpe.cin.dsoa.platform.handler.provider;

import java.util.Properties;

import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.handlers.providedservice.CreationStrategy;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

public class Strategy extends CreationStrategy {

	private InstanceManager m_manager;
	private ProviderMetadata p_metadata;

	public Object getService(Bundle bundle, ServiceRegistration registration) {
		return null; //AdaptationManager.createProxy(m_manager, p_metadata);
	}

	public void ungetService(Bundle bundle, ServiceRegistration registration,
			Object service) {
	}

	@Override
	public void onPublication(InstanceManager arg0, String[] arg1,
			Properties arg2) {

		m_manager = arg0;

		DsoaProvidesHandler providerManager = (DsoaProvidesHandler) m_manager
				.getHandler("br.ufpe.cin.dsoa.manager:provider-manager");

		p_metadata = providerManager.getProviderMetadata();

		/*
		 * transforma a string que representa a lista de SLOs em uma lista de
		 * SLOs String array = (String) arg2.get("provider.slos"); List<Slo>
		 * slos = new ArrayList<Slo>(); for (String description :
		 * array.split(";")) { slos.add(new Slo(description)); }
		 */
	}

	@Override
	public void onUnpublication() {
		// TODO Auto-generated method stub

	}
}
