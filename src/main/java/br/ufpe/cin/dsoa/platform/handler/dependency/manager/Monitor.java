package br.ufpe.cin.dsoa.platform.handler.dependency.manager;

import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.handler.dependency.Dependency;

public class Monitor {

	
	private DsoaPlatform dsoa;

	public Monitor(DsoaPlatform dsoa) {
		this.dsoa = dsoa;
	}

	public void instrument(Dependency dependency) {
		dependency.setProxy(dsoa.getProxyFactory());
	}


}
