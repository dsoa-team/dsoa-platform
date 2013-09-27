package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import org.osgi.service.event.EventAdmin;

import br.ufpe.cin.dsoa.api.event.EventType;
import br.ufpe.cin.dsoa.platform.handler.dependency.Dependency;
import br.ufpe.cin.dsoa.platform.monitor.DynamicProxyFactory;

public class Monitor {

	private EventType eventType;
	private EventAdmin eventAdmin;

	public void instrument(Dependency dependency) {
		DynamicProxyFactory dynamicProxy = new DynamicProxyFactory(dependency, eventAdmin,
				eventType);
		dependency.setDynamicProxy(dynamicProxy);

	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}
}
