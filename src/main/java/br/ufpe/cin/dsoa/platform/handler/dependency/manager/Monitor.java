package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import br.ufpe.cin.dsoa.platform.handler.dependency.Dependency;

public class Monitor {

	private DependencyManager depManager;

	public Monitor(DependencyManager depManager) {
		this.depManager = depManager;
	}

	public void instrument(Dependency dependency) {
		dependency.setProxy(depManager.getDsoaPlatform().getProxyFactory());
	}


}
