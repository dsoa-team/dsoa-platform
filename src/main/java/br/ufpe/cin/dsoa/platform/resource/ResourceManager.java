package br.ufpe.cin.dsoa.platform.resource;

import br.ufpe.cin.dsoa.api.service.Service;

public interface ResourceManager {

	void manage(Service service);

	void release(String serviceId);

}