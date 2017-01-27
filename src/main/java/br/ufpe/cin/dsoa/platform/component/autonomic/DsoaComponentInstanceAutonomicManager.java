package br.ufpe.cin.dsoa.platform.component.autonomic;

import java.util.HashMap;
import java.util.Map;

import br.ufpe.cin.dsoa.api.service.impl.BindingImpl;
import br.ufpe.cin.dsoa.platform.DsoaPlatform;
import br.ufpe.cin.dsoa.platform.handler.requires.DsoaBindingManager;

public class DsoaComponentInstanceAutonomicManager {

	private Map<String, DsoaBindingManager> bindingManagerMap = new HashMap<String, DsoaBindingManager>();
	
	private DsoaPlatform dsoaPlatform;
	
	public DsoaComponentInstanceAutonomicManager(DsoaPlatform platform) {
		this.dsoaPlatform = platform;
	}
	
	public DsoaBindingManager getBindingManager(BindingImpl bindingInstance) {
		DsoaBindingManager bindingManager = new DsoaBindingManager(dsoaPlatform, bindingInstance);
		this.bindingManagerMap.put(bindingInstance.getName(), bindingManager);
		return bindingManager;
	}

}
