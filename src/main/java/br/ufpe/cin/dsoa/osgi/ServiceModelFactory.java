package br.ufpe.cin.dsoa.osgi;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.ServiceReference;

import br.ufpe.cin.dsoa.contract.Service;
import br.ufpe.cin.dsoa.contract.ServiceImpl;
import br.ufpe.cin.dsoa.contract.ServiceMetadata;
import br.ufpe.cin.dsoa.handler.dependency.ServiceModel;

public abstract class ServiceModelFactory {
	
	public static ServiceModel createOsgiServiceModel(ServiceReference reference) {
		Service service = new ServiceImpl(reference);
		Map<String, Object> data = new HashMap<String, Object>();
		for(String key : reference.getPropertyKeys()) {
			data.put(key, reference.getProperty(key));
		}
		ServiceMetadata metadata = new ServiceMetadata(data);
		return new ServiceModel(service,metadata);
	}
}
