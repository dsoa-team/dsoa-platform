package br.ufpe.cin.dsoa.broker.util;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.Service;
import br.ufpe.cin.dsoa.contract.ServiceImpl;
import br.ufpe.cin.dsoa.contract.ServiceMetadata;

public abstract class ServiceModelFactory {
	
	public static Service createOsgiServiceModel(ServiceReference reference) {
		Map<String, Object> data = new HashMap<String, Object>();
		for(String key : reference.getPropertyKeys()) {
			data.put(key, reference.getProperty(key));
		}
		ServiceMetadata metadata = new ServiceMetadata(data);
		return new ServiceImpl(reference, metadata);
	}
}
